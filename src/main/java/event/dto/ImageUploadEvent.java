package event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageUploadEvent {
    String movieId;
    String fileName;
    String contentType;
    byte[] data;
    long fileSize;
}
