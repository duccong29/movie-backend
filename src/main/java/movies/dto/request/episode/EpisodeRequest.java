package movies.dto.request.episode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeRequest {
    @NotBlank(message = "TITLE_REQUIRED")
    @Size(max = 255, message = "TITLE_INVALID")
    String title;
    @NotNull(message = "EPISODE_REQUIRED")
    @Min(value = 1, message = "EPISODE_INVALID")
    Integer episodeNumber;

    Integer durationMinutes;
    @NotNull(message = "SEASON_REQUIRED")
    String seasonId;
}
