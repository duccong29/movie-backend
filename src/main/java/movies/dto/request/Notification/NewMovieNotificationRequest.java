package movies.dto.request.Notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewMovieNotificationRequest {
    String movieId;
    String title;
    String targetUserId;
}
