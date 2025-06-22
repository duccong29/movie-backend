package movies.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import movies.config.KafkaConfig;
import movies.constant.PredefinedNotification;
import movies.dto.response.NotificationResponse;
import movies.mapper.NotificationMapper;
import movies.repository.NotificationRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final KafkaTemplate<String, NotificationResponse> kafkaTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private static final Map<String, String> NOTIFICATION_TOPIC_MAP = Map.of(
            PredefinedNotification.TYPE_COMMENT, KafkaConfig.TOPIC_COMMENT_NOTIFICATION,
            PredefinedNotification.TYPE_COMMENT_REPLY, KafkaConfig.TOPIC_COMMENT_NOTIFICATION,
            PredefinedNotification.TYPE_NEW_CONTENT, KafkaConfig.TOPIC_NEW_CONTENT_NOTIFICATION,
            PredefinedNotification.TYPE_REVIEW, KafkaConfig.TOPIC_REVIEW_NOTIFICATION
            // Thêm các ánh xạ khác tại đây
    );

    public void publishNotification(NotificationResponse response) {
        persistNotification(response);
        sendToKafka(response);
    }

    private void persistNotification(NotificationResponse response) {
        try {
            notificationRepository.save(notificationMapper.toNotification(response));
        } catch (Exception e) {
            log.error("Failed to persist notification", e);
        }
    }

    private void sendToKafka(NotificationResponse response) {
        String topic = determineTopic(response.getNotificationType());

        try {
            String key = response.getUserId() != null ? response.getUserId() : "broadcast";
            kafkaTemplate.send(topic, key, response);
            log.debug("Notification published to Kafka topic {}: {}", topic, response.getTitle());
        } catch (Exception e) {
            log.error("Failed to publish notification to Kafka topic {}", topic, e);
        }
    }

    private String determineTopic(String notificationType) {
        return NOTIFICATION_TOPIC_MAP.getOrDefault(
                notificationType,
                KafkaConfig.TOPIC_DEFAULT_NOTIFICATION
        );
    }
}
