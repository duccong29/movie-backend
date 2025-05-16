package movies.dto.response.image;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageResponse {
    String id;
    String fileName;
    String originalFileName;
    String fileType;
    Long fileSize;
    String filePath;
    String cloudinaryUrl;
    String localUrl;
    String imageType;

    Boolean isStoredLocally;
    Boolean isStoredInCloudinary;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
