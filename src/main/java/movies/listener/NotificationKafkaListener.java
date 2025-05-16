package movies.listener;

import event.dto.ImageUploadEvent;
import event.dto.MovieProcessingEvent;
import event.dto.NotificationEvent;
import event.dto.VideoUploadEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.entity.Movie;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.repository.MovieRepository;
import movies.service.EmailService;
import movies.service.ImageService;
import movies.service.VideoService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationKafkaListener {
    EmailService emailService;
    VideoService videoService;
    ImageService imageService;
    MovieRepository movieRepository;

    @KafkaListener(topics = "email-notifications")
    public void handleEmailNotification(NotificationEvent message) {
        try {
            if (!"EMAIL".equalsIgnoreCase(message.getChannel())) return;

            Map<String, Object> param = message.getParam();
            if (param == null || !param.containsKey("token") || !param.containsKey("tokenType")) {
                log.warn("Thiếu thông tin token hoặc tokenType");
                return;
            }

            String token = (String) param.get("token");
            String tokenType = (String) param.get("tokenType");

            emailService.sendTokenEmail(
                    message.getRecipient(),
                    token,
                    tokenType
            );

        } catch (Exception e) {
            log.error("Gửi email thất bại: {}", e.getMessage(), e);
        }
    }

//    @KafkaListener(topics = "image-upload-topic")
//    public void handleImageUpload(ImageUploadEvent event) {
//        try {
//            imageService.uploadImageFromEvent(event);
//        } catch (IOException e) {
//            log.error("Failed to upload image for movie {}", event.getMovieId(), e);
//        }
//    }
//    @KafkaListener(topics = "video-upload-topic")
//    public void handleVideoUpload(VideoUploadEvent event) {
//        try {
//            videoService.uploadVideoFromEvent(event);
//        } catch (IOException e) {
//            log.error("Failed to upload video for movie {}", event.getMovieId(), e);
//        }
//    }
}
