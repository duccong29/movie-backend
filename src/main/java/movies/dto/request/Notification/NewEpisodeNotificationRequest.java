package movies.dto.request.Notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEpisodeNotificationRequest {
    String seriesId;
    String seasonId;
    String episodeId;
    String seriesTitle;
    String targetUserId;
}
