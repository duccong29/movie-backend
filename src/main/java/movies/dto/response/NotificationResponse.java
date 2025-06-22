package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
     String id;
     String title;
     String content;
     String notificationType;

     String userId;
     boolean readStatus;
     String targetUrl;

     LocalDateTime createdAt;
}
