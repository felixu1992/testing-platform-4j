package top.felixu.platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    private Duration timeout =  Duration.ofHours(2);

    /**
     * 请求头 key
     */
    private String authorization = "Authorization";

    /**
     * 拼在 jwt 前面的一个字符串
     */
    private String salt = "token ";

    /**
     * jwt 加密的 secret，同时也会作为使用 secret 认证的请求头
     */
    private String secret = "Testing-Platform";


    /**
     * 默认密码
     * 经过 {@link top.felixu.platform.util.Md5Utils#encode(String)} 加密后的结果
     * 如需自行指定默认密码，请按照上面提到的方式生成
     */
    private String defaultPassword = "e7181c3101c7dba1fce03b2edf3e0d05";

    /**
     * 忽略拦截的路径
     */
    private List<String> ignores = new ArrayList<>();
}
