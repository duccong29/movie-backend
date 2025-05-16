package movies.dto.response.video;

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
    //    String Id;
//    String filePath;
//    String hlsPath;
//    LocalDateTime createdAt;
//    String movieId;
     String id;
     String filePath;
     String hlsPath;
     String status;

     String fileName;
     String originalFileName;
     String fileType;
     Long fileSize;

     String cloudinaryPublicId;
     String cloudinaryUrl;
     String localUrl;

     Boolean isStoredLocally;
     Boolean isStoredInCloudinary;

     LocalDateTime createdAt;
    String movieId;
}
