package top.felixu.platform.util;

import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Report;

import java.util.List;
import java.util.Map;

/**
 * @author felixu
 * @since 2021.09.11
 */
public class ExecuteCaseUtils {

    public static List<Report> execute(List<CaseInfo> cases, List<CaseInfo> caseInfos) {

    }

    private static void doExecute(List<Report> reports, CaseInfo caseInfo) {
        List<Dependency> dependencies = caseInfo.getDependencies();
//        if (!CollectionUtils.isEmpty(dependencies))

    }

    private static void request(CaseInfo caseInfo, String url, Map<String, String> headers, Map<String, Object> params) {

    }
}