package top.felixu.platform.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import top.felixu.common.bean.BeanUtils;
import top.felixu.common.json.JsonUtils;
import top.felixu.platform.enums.CaseStatusEnum;
import top.felixu.platform.enums.HttpMethodEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.*;
import top.felixu.platform.service.FileInfoService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.11
 */
@Slf4j
public class ExecuteCaseUtils {

    /**
     * 用例执行
     *
     * @param project    用例所属项目
     * @param collection 当前项目下所有用例
     * @param caseInfos  需要被执行的用例
     * @return 当前执行产生的结果
     */
    public static List<Record> execute(Project project, List<CaseInfo> collection, List<CaseInfo> caseInfos) {
        if (CollectionUtils.isEmpty(collection) || CollectionUtils.isEmpty(caseInfos))
            return Collections.emptyList();
        Map<Integer, Record> reportMap = new HashMap<>();
        caseInfos.forEach(caseInfo ->
                doExecute(project, collection.stream().collect(Collectors.toMap(CaseInfo::getId, Function.identity())), reportMap, caseInfo)
        );
        return new ArrayList<>(reportMap.values());
    }

    /**
     * 单个用例执行逻辑
     *
     * @param project   用例所属项目
     * @param caseMap   项目下所有用例集合
     * @param reportMap 执行过的用例集合
     * @param caseInfo  当前需要执行的用例
     */
    private static void doExecute(Project project, Map<Integer, CaseInfo> caseMap, Map<Integer, Record> reportMap, CaseInfo caseInfo) {
        // 得到当前用例的依赖
        List<Dependency> dependencies = caseInfo.getDependencies();
        // 构建结果
        Record record = BeanUtils.map(caseInfo, Record.class);
        record.setExecuted(true);
        record.setTimeUsed(0L);
        record.setResponseContent(Collections.emptyMap());
        record.setHttpStatus(0);
        record.setCaseId(caseInfo.getId());
        record.setId(null);
        record.setStatus(CaseStatusEnum.FAILED);
        reportMap.put(record.getCaseId(), record);
        // 判断当前用例是否需要执行
        if (!Boolean.TRUE.equals(caseInfo.getRun())) {
            // 不需要执行
            record.setStatus(CaseStatusEnum.IGNORED);
            return;
        }
        // 得到请求参数
        Map<String, Object> params = ValueUtils.emptyAs(caseInfo.getParams(), new HashMap<>());
        // 计算真正地入参
        buildParams(params, dependencies, reportMap, project, caseMap);
        // 计算请求头
        HttpHeaders headers = buildHeaders(project.getHeaders(), record.getHeaders());
        // 计算真正的 url
        String url = buildUrl(project, caseInfo, params);
        // 是否需要延时
        if (caseInfo.getDelay() > 0) {
            try {
                Thread.sleep(caseInfo.getDelay() * 1000L);
            } catch (InterruptedException ex) {
                log.error("---> Delayed execution request failed: {}", ex.getLocalizedMessage());
            }
        }
        // 执行请求，获得请求结果
        request(caseInfo.getMethod(), url, headers, params, record);
        // 计算用例执行结果
        checkStatus(project, record, caseMap, reportMap);
    }

    private static void checkStatus(Project project, Record record, Map<Integer, CaseInfo> caseMap, Map<Integer, Record> reportMap) {
        // 需要校验状态码，状态码没过，直接失败
        if (record.getCheckStatus() && !record.getExpectedHttpStatus().equals(record.getHttpStatus())) {
            record.setStatus(CaseStatusEnum.FAILED);
            return;
        }
        // 没有需要校验的结果，直接成功
        List<Expected> expects = record.getExpects();
        if (CollectionUtils.isEmpty(expects)) {
            record.setStatus(CaseStatusEnum.PASSED);
            return;
        }
        // 根据预期结果，计算是成功还是失败
        record.setStatus(expects.stream().allMatch(expected -> {
            // 得到要校验的结果
            Object result = ValueUtils.getValue(expected.getExpectKey(), record.getResponseContent());
            // 计算需要对比的值
            Object expect;
            Expected.ExpectValue expectValue = expected.getExpectValue();
            // 固定值对比
            if (expectValue.isFixed()) {
                Object value = expectValue.getValue();
                if (value instanceof String && ValueUtils.isNumber((String) value))
                    expect = Integer.valueOf((String) value);
                else
                    expect = value;
            } else {
                // 动态取值对比
                Integer depend = expectValue.getDepend();
                if (reportMap.get(depend) == null || !reportMap.get(depend).isExecuted())
                    doExecute(project, caseMap, reportMap, caseMap.get(depend));
                expect = ValueUtils.getValue(expectValue.getSteps(), reportMap.get(depend).getResponseContent());
            }
            // 全是空，成功
            if (expect == null && result == null)
                return true;
            // 全不是空，对比结果
            if (expect != null && result != null)
                return expect.equals(result);
            // 其他情况失败
            return false;
        }) ? CaseStatusEnum.PASSED : CaseStatusEnum.FAILED);
    }

    private static void request(HttpMethodEnum method, String url, HttpHeaders headers, Map<String, Object> params, Record record) {
        RestTemplate restTemplate = ApplicationUtils.getBean(RestTemplate.class);
        HttpEntity entity;
        if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(headers.getContentType())) {
            MultiValueMap<String, Object> realParam = new LinkedMultiValueMap<>();
            params.forEach(realParam::add);
            entity = new HttpEntity<>(realParam, headers);
        } else {
            entity = new HttpEntity<>(params, headers);
        }
        // TODO: 11/11 执行报错需要处理
        long start = System.currentTimeMillis();
        ResponseEntity<Map> resp = restTemplate.exchange(URI.create(url), HttpMethod.valueOf(method.getDesc()), entity, Map.class);
        record.setTimeUsed(System.currentTimeMillis() - start);
        record.setHttpStatus(resp.getStatusCodeValue());
        record.setResponseContent(ValueUtils.emptyAs((Map<String, Object>) resp.getBody(), new HashMap<>()));
    }

    private static void buildParams(Map<String, Object> params, List<Dependency> dependencies, Map<Integer, Record> reportMap,
                                    Project project, Map<Integer, CaseInfo> caseMap) {
        if (!CollectionUtils.isEmpty(dependencies)) {
            // 计算真正地入参
            JsonUtils.fromJsonToList(JsonUtils.toAlwaysJson(dependencies), Dependency.class).forEach(dependency -> {
                // 确定依赖的用例已经执行过了
                Integer depend = dependency.getDependValue().getDepend();
                if (reportMap.get(depend) == null || !reportMap.get(depend).isExecuted())
                    doExecute(project, caseMap, reportMap, caseMap.get(depend));
                // 得到需要插入的值
                Object value = ValueUtils.getValue(dependency.getDependValue().getSteps(),
                        reportMap.get(depend).getResponseContent());
                ValueUtils.setValue(params, dependency.getDependKey(), value);
            });
        }
        // 获取文件参数
        FileInfoService fileInfoService = ApplicationUtils.getBean(FileInfoService.class);
        params.entrySet().parallelStream().forEach(entry -> {
            Object value = entry.getValue();
            if (value instanceof String && ((String) value).startsWith("file:")) {
                String val = (String) value;
                FileInfo fileInfo = fileInfoService.getFileInfoByIdAndCheck(Integer.valueOf(val.replace("file:", "")));
                FileSystemResource fsr = new FileSystemResource(fileInfo.getPath());
                entry.setValue(fsr);
            }
        });
    }

    private static HttpHeaders buildHeaders(Map<String, String> parentHeaders, Map<String, String> selfHeaders) {
        // 以项目的请求头为基础
        if (parentHeaders == null)
            parentHeaders = new HashMap<>(8);
        // 将用例自身的加入到项目的请求头中，如果重复，以用例自身的为准
        if (!CollectionUtils.isEmpty(selfHeaders))
            parentHeaders.putAll(selfHeaders);
        HttpHeaders httpHeaders = new HttpHeaders();
        String contentType = parentHeaders.getOrDefault("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.setContentType(MediaType.parseMediaType(contentType));
        parentHeaders.remove("Content-Type");
        String accept = parentHeaders.get("Accept");
        if (accept != null)
            httpHeaders.setAccept(Arrays.stream(accept.split(";")).map(MediaType::parseMediaType).collect(Collectors.toList()));
        parentHeaders.remove("Accept");
        parentHeaders.forEach(httpHeaders::add);
        return httpHeaders;
    }

    private static String buildUrl(Project project, CaseInfo caseInfo, Map<String, Object> params) {
        // 得到真正的 host
        String host = ValueUtils.emptyAs(caseInfo.getHost(), project.getHost());
        if (ObjectUtils.isEmpty(host))
            throw new PlatformException(ErrorCode.CASE_HOST_IS_ERROR, caseInfo.getName());
        // 替换 url 中的占位符
        String url = caseInfo.getPath();
        Matcher matcher = Pattern.compile("[{](.*?)[}]", Pattern.DOTALL).matcher(url);
        while (matcher.find())
            url = url.replace(matcher.group(), ValueUtils.emptyAs(String.valueOf(params.get(matcher.group(1))), matcher.group()));
        return host + url;
    }
}