package movies.service;

import event.dto.NotificationEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedRole;
import movies.constant.PredefinedToken;
import movies.dto.request.user.UserCreationRequest;
import movies.dto.request.user.UserUpdateRequest;
import movies.dto.response.user.ForgotPasswordResponse;
import movies.dto.response.PageResponse;
import movies.dto.request.user.ForgotPasswordRequest;
import movies.dto.response.user.UserResponse;
import movies.entity.Role;
import movies.entity.User;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.UserMapper;
import movies.repository.RoleRepository;
import movies.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    AuthenticationService authenticationService;
    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        validateUserRequest(request);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String username = generateUsername(request);
        user.setUsername(username);

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCodes.UNCATEGORIZED_EXCEPTION);
        }

        sendTokenToUser(user, PredefinedToken.VERIFICATION_TOKEN, "CONFIRM_EMAIL");

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse getMyInfo() {
        String userId = authenticationService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUser(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> userResponses = userPage.getContent().stream().map(userMapper::toUserResponse).toList();

        return PageResponse.<UserResponse>builder().currentPage(page).pageSize(size).totalElements(userPage.getTotalElements()).totalPages(userPage.getTotalPages()).data(userResponses).build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED)));
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCodes.USER_NOT_EXISTED);
        }
        userRepository.deleteById(userId);
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCodes.EMAIL_NOT_EXISTED));

        if (!user.isEnabled()) {
            throw new AppException(ErrorCodes.EMAIL_NOT_CONFIRMED);
        }

        sendTokenToUser(user, PredefinedToken.PASSWORD_RESET_TOKEN, "FORGOT_PASSWORD");

        return ForgotPasswordResponse.builder()
                .status(true)
                .message("Password reset email sent successfully.")
                .build();
    }

    private void sendTokenToUser(User user, String tokenType, String subject) {
        String token = authenticationService.generateEmailToken(user, tokenType);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(user.getEmail())
                .subject(subject)
                .body("Hello, " + user.getUsername())
                .param(Map.of("token", token,
                        "tokenType", tokenType))
                .build();

        kafkaTemplate.send("email-notifications", notificationEvent);
    }


    private void validateUserRequest(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCodes.EMAIL_EXISTED);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCodes.USER_EXISTED);
        }
    }

    private String generateUsername(UserCreationRequest request) {
        return Optional.ofNullable(request.getUsername()).filter(s -> !s.isBlank()).orElseGet(() -> {
            String email = request.getEmail();
            int atIndex = email.indexOf('@');
            return atIndex != -1 ? email.substring(0, atIndex) : email;
        });
    }

}
