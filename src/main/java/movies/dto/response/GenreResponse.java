package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.entity.Series;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreResponse {
    String id;
    String name;
    Set<MovieResponse> movies;
    Set<SeriesResponse> series;
    LocalDateTime createdAt;
}
