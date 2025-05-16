package movies.dto.response.season;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.episode.EpisodeResponse;
import movies.dto.response.series.SeriesNameResponse;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeasonResponse {
    String id;
    Integer seasonNumber;
    String title;
    String posterUrl;
    SeriesNameResponse series;
    List<EpisodeResponse> episodes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
