package movies.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeriesRequest {
    String title;
    String synopsis;
    String posterUrl;
    String country;
    String language;

    Set<String> genreIds;
}
