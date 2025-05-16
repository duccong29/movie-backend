package movies.dto.response.series;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.genre.GenreNamesResponse;
import movies.dto.response.season.SeasonNameResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeriesResponse {
    String id;
    String title;
    String posterUrl;
    String country;
    Double averageRating;
    Set<GenreNamesResponse> genres;
    List<SeasonNameResponse> seasons;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
