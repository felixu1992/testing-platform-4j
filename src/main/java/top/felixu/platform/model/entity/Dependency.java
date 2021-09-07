package top.felixu.platform.model.entity;

import lombok.Data;

/**
 * 用例依赖
 *
 * @author felixu
 * @since 2021.09.07
 */
@Data
public class Dependency {

    private String[] dependKey;

    private DependValue dependValue;

    @Data
    public static class DependValue {

        private Integer depend;

        private String[] steps;
    }
}