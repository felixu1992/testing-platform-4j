package top.felixu.platform.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 用例依赖
 *
 * @author felixu
 * @since 2021.09.07
 */
@Data
public class Dependency {

    /**
     * 需要被插入的位置
     */
    private String[] dependKey;

    /**
     * 需要被插入的值
     */
    private DependValue dependValue;

    @Data
    @Builder
    public static class DependValue {

        /**
         * 依赖的用例
         */
        private Integer depend;

        /**
         * 取值过程
         */
        private String[] steps;
    }
}