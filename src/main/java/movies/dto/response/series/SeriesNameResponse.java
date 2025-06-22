package movies.dto.response.series;

import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.genre.GenreNamesResponse;
import movies.dto.response.image.ImageResponse;
import movies.dto.response.season.SeasonNameResponse;
import movies.dto.response.season.SeasonResponse;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeriesNameResponse {
    String id;
    String title;
    String country;
    Double averageRating;
    Set<GenreNamesResponse> genres;
    List<SeasonNameResponse> seasons;
    List<ImageResponse> images;
}
