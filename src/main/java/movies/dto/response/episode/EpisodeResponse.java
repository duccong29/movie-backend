package movies.dto.response.episode;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.video.VideoPathResponse;
import movies.dto.response.video.VideoResponse;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeResponse {
    String id;
    String title;
    Integer episodeNumber;
    Integer durationMinutes;
    String seasonId;

    List<VideoResponse> video;
}
