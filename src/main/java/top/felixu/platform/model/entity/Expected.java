package top.felixu.platform.model.entity;

import lombok.Data;

/**
 *
 *
 * @author felixu
 * @since 2021.09.07
 */
@Data
public class Expected {

    private String[] expectKey;

    private ExpectValue expectValue;

    @Data
    public static class ExpectValue {

        private String[] steps;

        private String value;

        private boolean fixed;
    }
}