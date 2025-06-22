package movies.dto.request.Notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentReplyNotificationRequest {
    String commentId;
    String contentId;
    String contentType;
    String targetUserId;
}
