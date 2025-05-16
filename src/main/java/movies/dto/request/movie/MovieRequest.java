package movies.dto.request.movie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {
    @NotBlank(message = "TITLE_REQUIRED")
    @Size(max = 255, message = "TITLE_INVALID")
    String title;
    String description;
    Integer durationMinutes;
    LocalDate releaseDate;
    String country;
//    List<MultipartFile> images;
    Set<String> genreIds;
}
