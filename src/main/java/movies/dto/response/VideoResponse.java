package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoResponse {
    String Id;
    String filePath;
    String hlsPath;
    LocalDateTime createdAt;
    String movieId;
}
