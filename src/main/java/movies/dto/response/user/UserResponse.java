package movies.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import movies.dto.response.RoleResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String email;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    boolean enabled;
    Set<RoleResponse> roles;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
