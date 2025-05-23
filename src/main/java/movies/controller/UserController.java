package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import movies.dto.request.user.ForgotPasswordRequest;
import movies.dto.request.user.ResetPasswordRequest;
import movies.dto.request.user.UserCreationRequest;
import movies.dto.request.user.UserUpdateRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.PageResponse;
import movies.dto.response.user.ForgotPasswordResponse;
import movies.dto.response.user.ResetPasswordResponse;
import movies.dto.response.user.UserResponse;
import movies.service.AuthenticationService;
import movies.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    AuthenticationService authenticationService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(userId, request))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<UserResponse>> getAllUser(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .data(userService.getAllUser(page, size))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserById(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .data("User has been deleted")
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ApiResponse.<ForgotPasswordResponse>builder()
                .data(userService.forgotPassword(request))
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ApiResponse.<ResetPasswordResponse>builder()
                .data(authenticationService.resetPassword(request))
                .build();
    }
}
