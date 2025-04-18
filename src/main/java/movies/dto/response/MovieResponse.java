package movies.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResponse {
    String id;
    String title;
    String description;
    Integer durationMinutes;
    LocalDate releaseDate;
    String posterUrl;
    String country;
    String language;
    Double averageRating;
    Set<GenreNamesResponse> genres;
    List<VideoPathResponse> videoPaths;
//    String filePath;
//    String hlsPath;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
