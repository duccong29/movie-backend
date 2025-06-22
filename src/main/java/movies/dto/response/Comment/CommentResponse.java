package movies.dto.response.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.user.UserResponse;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
     String id;
     String text;
     UserResponse user;
     String movieId;
     String seriesId;
     String episodeId;
     String parentCommentId;
     LocalDateTime createdAt;
     LocalDateTime updatedAt;
}
