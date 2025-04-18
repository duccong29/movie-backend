package movies.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Email(message = "INVALID_EMAIL")
    String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    String firstName;
    String lastName;

    @Past(message = "INVALID_DOB")
    LocalDate dob;
}
