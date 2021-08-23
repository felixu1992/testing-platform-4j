package top.felixu.platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author felixu
 * @since 2021.08.23
 */
@Data
@ConfigurationProperties(prefix = "platform.permission")
public class PermissionProperties {

    /**
     * 超时时间
     */
    private Duration timeout =  Duration.of(2, ChronoUnit.HOURS);

    /**
     * 白名单
     */
    private List<String> whiteList = new ArrayList<>();
}
