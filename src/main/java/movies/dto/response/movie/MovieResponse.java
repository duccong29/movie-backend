package movies.dto.response.movie;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.image.ImageResponse;
import movies.dto.response.genre.GenreNamesResponse;
import movies.dto.response.video.VideoResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponse {
    String id;
    String title;
    String description;
    Integer durationMinutes;
    LocalDate releaseDate;
    String country;
    Double averageRating;

    Set<GenreNamesResponse> genres;
    VideoResponse video;
    List<ImageResponse> images;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
