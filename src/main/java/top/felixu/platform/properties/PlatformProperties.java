package top.felixu.platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author felixu
 * @since 2021.09.04
 */
@Data
@ConfigurationProperties(prefix = "platform")
public class PlatformProperties {

    private String fileStorage = "/home/testing-platform/files";
}
