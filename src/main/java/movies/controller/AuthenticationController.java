package movies.controller;

import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.authen.AuthenticationRequest;
import movies.dto.request.authen.IntrospectRequest;
import movies.dto.request.authen.LogoutRequest;
import movies.dto.request.authen.RefreshRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.authen.AuthenticationResponse;
import movies.dto.response.authen.IntrospectResponse;
import movies.dto.response.user.UserConfirmResponse;
import movies.service.AuthenticationService;
import movies.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/outbound/authentication")
    ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam("code") String code) {
        var result = authenticationService.outboundAuthenticate(code);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request){
        var result= authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().data(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().data(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/verify-email")
    ApiResponse<UserConfirmResponse> confirmEmail(@RequestParam("token") String token) {
        return ApiResponse.<UserConfirmResponse>builder()
                .data(authenticationService.verifyEmailToken(token))
                .build();
    }

//    @GetMapping("/verify-email")
//    public ApiResponse<MessageResponse> verifyEmail(@RequestParam String token) {
//        return ApiResponse.ok(userService.verifyEmail(token));
//    }
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
//        return ResponseEntity.ok(userService.forgotPassword(request));
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
//        return ResponseEntity.ok(userService.resetPassword(request));
//    }
}
