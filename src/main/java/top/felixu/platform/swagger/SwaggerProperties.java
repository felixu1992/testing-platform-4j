package top.felixu.platform.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import static top.felixu.platform.swagger.SwaggerConstants.Config.PREFIX;

/**
 * Swagger 配置类
 *
 * @author felixu
 * @since  2021.08.07
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class SwaggerProperties {

    /**
     * 是否开启 swagger
     */
    private Boolean enabled = true;

    /**
     * swagger 会解析的包路径
     **/
    private String basePackage = "top.felixu.*.controller";

    /**
     * 标题
     **/
    private String title = "";

    /**
     * 描述
     **/
    private String description = "";

    /**
     * 版本
     **/
    private String version = "";

    /**
     * 许可证
     **/
    private String license = "";

    /**
     * 许可证 URL
     **/
    private String licenseUrl = "";

    /**
     * 服务条款 URL
     **/
    private String termsOfServiceUrl = "";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    @Data
    public static class Contact {

        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人 url
         **/
        private String url = "";
        /**
         * 联系人 email
         **/
        private String email = "";
    }
}
