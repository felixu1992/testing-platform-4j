package top.felixu.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import top.felixu.platform.properties.PermissionProperties;
import top.felixu.platform.properties.PlatformProperties;

/**
 * @author felixu
 * @since 2021.08.23
 */
//@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties(PlatformProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
