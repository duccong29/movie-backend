package movies.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeasonResponse {
    String id;
    Integer seasonNumber;
//    String seriesId;
    List<EpisodeResponse> episodes;
}
