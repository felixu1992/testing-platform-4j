package top.felixu.platform.util;

import cn.hutool.core.util.ReUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.enums.CaseStatusEnum;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Project;
import top.felixu.platform.model.entity.Report;

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
public class ExecuteCaseUtils {

    /**
     * 用例执行
     *
     * @param project    用例所属项目
     * @param collection 当前项目下所有用例
     * @param caseInfos  需要被执行的用例
     * @return 当前执行产生的结果
     */
    public static List<Report> execute(Project project, List<CaseInfo> collection, List<CaseInfo> caseInfos) {
        if (CollectionUtils.isEmpty(collection) || CollectionUtils.isEmpty(caseInfos))
            return Collections.emptyList();
        Map<Integer, Report> reportMap = new HashMap<>();
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
    private static void doExecute(Project project, Map<Integer, CaseInfo> caseMap, Map<Integer, Report> reportMap, CaseInfo caseInfo) {
        // 得到当前用例的依赖
        List<Dependency> dependencies = caseInfo.getDependencies();
        // 构建结果
        Report report = BeanUtils.map(caseInfo, Report.class);
        report.setExecuted(true);
        report.setTimeUsed(0);
        report.setResponseContent(Collections.emptyMap());
        report.setHttpStatus(0);
        report.setCaseId(caseInfo.getId());
        report.setId(null);
        report.setStatus(CaseStatusEnum.FAILED);
        reportMap.put(report.getId(), report);
        // 判断当前用例是否需要执行
        if (!Boolean.TRUE.equals(caseInfo.getRun())) {
            // 不需要执行
            report.setStatus(CaseStatusEnum.IGNORED);
            return;
        }
        // 得到请求参数
        Map<String, Object> params = ValueUtils.nullAs(caseInfo.getParams(), new HashMap<>());
        // 如果依赖的用例集合不为空
        if (!CollectionUtils.isEmpty(dependencies)) {
            dependencies.parallelStream()
                    .map(Dependency::getDependValue)
                    .map(Dependency.DependValue::getDepend)
                    .forEach(dependencyId -> {
                        // 如果依赖的用例没有执行过
                        if (reportMap.get(dependencyId) == null || !reportMap.get(dependencyId).isExecuted())
                            doExecute(project, caseMap, reportMap, caseMap.get(dependencyId));
                    });
            // 计算真正的入参
        }
        // 计算请求头
        HttpHeaders headers = buildHeaders(project.getHeaders(), report.getHeaders());
        // 计算真正的 url
        String url = buildUrl(project, caseInfo, params);
        // 是否需要延时
        if (caseInfo.getDelay() > 0)
            Thread.sleep(caseInfo.getDelay() * 1000L);
        // 计算用例执行结果
    }

    private static void request() {
        RestTemplate restTemplate = ApplicationUtils.getBean(RestTemplate.class);
//        RequestCallback callback = request -> {
//
//        }
//        restTemplate.execute()
    }

    private static Map<String, Object> buildParams(Map<String, Object> params, List<Dependency> dependencies, Map<Integer, Report> reportMap) {
        if (params == null)
            params = new HashMap<>();
        dependencies.parallelStream().forEach(dependency -> {
            Dependency.DependValue depend = dependency.getDependValue();

        });
        return params;
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
        String host = ValueUtils.nullAs(caseInfo.getHost(), project.getHost());
        if (ObjectUtils.isEmpty(host))
            throw new PlatformException(ErrorCode.CASE_HOST_IS_ERROR, caseInfo.getName());
        Map<String, String> patterns = new HashMap<>();
        final String[] path = {caseInfo.getPath()};
        Matcher matcher = Pattern.compile("[{](.*?)[}]", Pattern.DOTALL).matcher(path[0]);
        while (matcher.find())
            patterns.put(matcher.group(1), matcher.group());
        patterns.forEach((k, v) -> path[0] = path[0].replace(v, ValueUtils.nullAs(String.valueOf(params.get(k)), v)));
        return host + path[0];
    }
}