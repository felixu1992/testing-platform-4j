package top.felixu.platform.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.felixu.platform.service.CaseInfoService;

/**
 * @author felixu
 * @since 2021.09.07
 */
@Service
@RequiredArgsConstructor
public class CaseInfoManager {

    private final CaseInfoService caseInfoService;
}