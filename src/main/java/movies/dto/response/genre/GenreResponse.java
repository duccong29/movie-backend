package movies.dto.response.genre;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.movie.MovieNameResponse;
import movies.dto.response.series.SeriesNameResponse;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenreResponse {
    String id;
    String name;
    Set<MovieNameResponse> movies;
    Set<SeriesNameResponse> series;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
