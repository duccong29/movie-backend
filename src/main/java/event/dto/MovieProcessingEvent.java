package event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieProcessingEvent {
     String movieId;
     List<String> tempImageUrls;
     String tempVideoUrl;
}
