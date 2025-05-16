package movies.dto.response.season;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeasonNameResponse {
    String id;
    Integer seasonNumber;
    String title;
}
