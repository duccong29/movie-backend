package movies.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.config.KafkaConfig;
import movies.dto.response.NotificationResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationKafkaListener {
//    EmailService emailService;
//
//    @KafkaListener(topics = "email-notifications")
//    public void handleEmailNotification(NotificationEvent message) {
//        try {
//            if (!"EMAIL".equalsIgnoreCase(message.getChannel())) return;
//
//            Map<String, Object> param = message.getParam();
//            if (param == null || !param.containsKey("token") || !param.containsKey("tokenType")) {
//                log.warn("Thiếu thông tin token hoặc tokenType");
//                return;
//            }
//
//            String token = (String) param.get("token");
//            String tokenType = (String) param.get("tokenType");
//
//            emailService.sendTokenEmail(
//                    message.getRecipient(),
//                    token,
//                    tokenType
//            );
//
//        } catch (Exception e) {
//            log.error("Gửi email thất bại: {}", e.getMessage(), e);
//        }
//    }

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Listen for notifications on the comment notification topic.
     *
     * @param notification The notification received from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_COMMENT_NOTIFICATION)
    public void handleCommentNotification(NotificationResponse notification) {
        log.info("Received comment notification: {}", notification);
        routeNotification(notification);
    }

    /**
     * Listen for notifications on the review notification topic.
     *
     * @param notification The notification received from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_REVIEW_NOTIFICATION)
    public void handleReviewNotification(NotificationResponse notification) {
        log.info("Received review notification: {}", notification);
        routeNotification(notification);
    }

    /**
     * Listen for notifications on the new content notification topic.
     *
     * @param notification The notification received from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_NEW_CONTENT_NOTIFICATION)
    public void handleNewContentNotification(NotificationResponse notification) {
        log.info("Received new content notification: {}", notification);
        routeNotification(notification);
    }

    /**
     * Listen for notifications on the system notification topic.
     *
     * @param notification The notification received from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_SYSTEM_NOTIFICATION)
    public void handleSystemNotification(NotificationResponse notification) {
        log.info("Received system notification: {}", notification);
        routeNotification(notification);
    }

    /**
     * Route the notification to the appropriate WebSocket destination.
     *
     * @param notification The notification to route
     */
    private void routeNotification(NotificationResponse notification) {
        try {
            // Route to a specific user if userId is provided
            if (notification.getUserId() != null) {
                String destination = "/queue/notifications." + notification.getUserId();
                messagingTemplate.convertAndSend(destination, notification);
                log.debug("Sent notification to user destination: {}", destination);
            } else {
                // Broadcast to all users
                messagingTemplate.convertAndSend("/topic/notifications", notification);
                log.debug("Broadcast notification to all users");
            }
        } catch (Exception e) {
            log.error("Failed to route notification: {}", e.getMessage(), e);
        }
    }
}
