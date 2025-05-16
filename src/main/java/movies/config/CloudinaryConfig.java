package movies.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CloudinaryConfig {

    @Value("${spring.cloudinary.cloud_name}")
    String cloudName;

    @Value("${spring.cloudinary.api_key}")
    String apiKey;

    @Value("${spring.cloudinary.api_secret}")
    String apiSecret;

//    @Value("${spring.image.upload.dir:./uploads/images}")
//    String uploadDir;

    @Bean
    public Cloudinary cloudinary() {
        if (cloudName == null || apiKey == null || apiSecret == null ||
                "your-cloud-name".equals(cloudName) ||
                "your-api-key".equals(apiKey) ||
                "your-api-secret".equals(apiSecret)) {

            log.warn("Cloudinary credentials not properly configured. Using a placeholder instance. " +
                    "Image uploads will fail and fall back to local storage. " +
                    "To enable Cloudinary storage, please provide proper credentials.");

            return new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "placeholder",
                    "api_key", "placeholder",
                    "api_secret", "placeholder",
                    "secure", true
            ));
        }

        log.info("Cloudinary configured with cloud name: {}", cloudName);
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

//    @Bean
//    public void createUploadDir() {
//        File dir = new File(uploadDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//    }
//
//    public String getUploadDir() {
//        return uploadDir;
//    }
}
