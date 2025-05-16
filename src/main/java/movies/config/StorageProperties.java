package movies.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "local.upload")
@Data
public class StorageProperties {
    private String uploadDir = "/uploads/images";
}
