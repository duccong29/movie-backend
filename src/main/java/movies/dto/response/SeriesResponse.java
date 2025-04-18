package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String synopsis;
    String posterUrl;
    String country;
    String language;
    Double averageRating;
    Set<GenreNamesResponse> genres;
    List<SeasonResponse> seasons;
//    List<EpisodeResponse> episodes;
    LocalDateTime createdAt;
}
