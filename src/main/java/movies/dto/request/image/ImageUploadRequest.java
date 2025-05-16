package movies.dto.request.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {

    private String entityType;

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @NotNull(message = "Image type is required")
    @NotBlank(message = "Image type cannot be blank")
    private String imageType; // poster, avatar, thumbnail, etc.
}
