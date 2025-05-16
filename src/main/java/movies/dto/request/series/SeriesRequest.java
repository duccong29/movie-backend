package movies.dto.request.series;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "TITLE_REQUIRED")
    @Size(max = 255, message = "TITLE_INVALID")
    String title;
    String description;
    String posterUrl;
    String country;

    Set<String> genreIds;
}
