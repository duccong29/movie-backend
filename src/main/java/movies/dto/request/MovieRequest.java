package movies.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {
    String title;
    String description;
    Integer durationMinutes;
    LocalDate releaseDate;
    String posterUrl;
    String country;
    String language;

    Set<String> genreIds;
}
