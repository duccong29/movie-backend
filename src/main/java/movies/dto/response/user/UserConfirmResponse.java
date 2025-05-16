package movies.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConfirmResponse {
    boolean status;
    String message;
    LocalDateTime expiresAt;
}
