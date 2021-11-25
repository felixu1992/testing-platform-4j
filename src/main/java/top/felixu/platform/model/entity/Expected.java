package top.felixu.platform.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author felixu
 * @since 2021.09.07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expected {

    /**
     * 预期校验的 key
     */
    private String[] expectKey;

    /**
     * 预期的值
     */
    private ExpectValue expectValue;

    @Data
    public static class ExpectValue {

        /**
         * 取值过程
         */
        private String[] steps;

        /**
         * 依赖的用例
         */
        private Integer depend;

        /**
         * 固定值
         */
        private Object value;

        /**
         * 是否是固定值
         */
        private boolean fixed;
    }
}