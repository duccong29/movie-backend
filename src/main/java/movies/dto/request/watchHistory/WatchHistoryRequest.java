package movies.dto.request.watchHistory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryRequest {
//    @NotBlank(message = "Movie ID is required for movie watch history")
    String movieId;

//    String seriesId;
    String episodeId;

    @Min(value = 0, message = "Watch time cannot be negative")
    Integer watchTimeSeconds;

    @Positive(message = "Total duration must be positive")
    Integer totalDurationSeconds;
}
