package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Integer duration;
    String seasonId;

    List<VideoPathResponse> videoPaths;
}
