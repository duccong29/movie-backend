package movies.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Topic names as constants
    public static final String TOPIC_COMMENT_NOTIFICATION = "comment-notifications";
    public static final String TOPIC_REVIEW_NOTIFICATION = "review-notifications";
    public static final String TOPIC_NEW_CONTENT_NOTIFICATION = "new-content-notifications";
    public static final String TOPIC_SYSTEM_NOTIFICATION = "system-notifications";
    public static final String TOPIC_DEFAULT_NOTIFICATION = "default-notifications";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentNotificationTopic() {
        return TopicBuilder.name(TOPIC_COMMENT_NOTIFICATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reviewNotificationTopic() {
        return TopicBuilder.name(TOPIC_REVIEW_NOTIFICATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newContentNotificationTopic() {
        return TopicBuilder.name(TOPIC_NEW_CONTENT_NOTIFICATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic systemNotificationTopic() {
        return TopicBuilder.name(TOPIC_SYSTEM_NOTIFICATION)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
