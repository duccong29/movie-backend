package movies.dto.response.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    String id;
    String fileName;
    String originalFileName;
    String fileType;
    Long fileSize;
    String filePath;
    String cloudinaryUrl;
    String localUrl;
    String imageType;

    // Entity information
    String entityType;
    String entityId;
    String entityName; // Movie title, Series title, Episode title, or Username

    Boolean isStoredLocally;
    Boolean isStoredInCloudinary;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
