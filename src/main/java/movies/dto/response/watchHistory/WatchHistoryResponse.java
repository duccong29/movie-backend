package movies.dto.response.watchHistory;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.episode.EpisodeResponse;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.series.SeriesNameResponse;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryResponse {
    String id;
    Integer watchTimeSeconds;
    Integer totalDurationSeconds;
    Double watchProgress;
    LocalDateTime lastWatchedAt;

    String userId;
    MovieResponse movie;
    EpisodeResponse episode;
    SeriesNameResponse series;


    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
