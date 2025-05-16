package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.response.ApiResponse;
import movies.dto.response.user.UserConfirmResponse;
import movies.service.AuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    AuthenticationService authenticationService;

    @GetMapping("/confirm")
    ApiResponse<UserConfirmResponse> confirmEmail(@RequestParam("token") String token) {
        return ApiResponse.<UserConfirmResponse>builder()
                .data(authenticationService.verifyEmailToken(token))
                .build();
    }
}
