package movies.dto.request.season;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeasonCreationRequest {
    @NotNull(message = "SEASON_REQUIRED")
    @Min(value = 1, message = "SEASON_INVALID")
    Integer seasonNumber;
    @NotNull(message = "SERIES_REQUIRED")
    String seriesId;
    String title;
    String posterUrl;
}
