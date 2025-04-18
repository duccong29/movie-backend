package movies.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeRequest {
    String title;
    Integer episodeNumber;
    Integer duration;
    String seasonId;
}
