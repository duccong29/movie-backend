package movies.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedRole;
import movies.constant.PredefinedToken;
import movies.dto.request.authen.*;
import movies.dto.request.user.ResetPasswordRequest;
import movies.dto.response.authen.AuthenticationResponse;
import movies.dto.response.authen.IntrospectResponse;
import movies.dto.response.user.UserConfirmResponse;
import movies.dto.response.user.ResetPasswordResponse;
import movies.entity.InvalidatedToken;
import movies.entity.Role;
import movies.entity.User;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.repository.InvalidatedTokenRepository;
import movies.repository.UserRepository;
import movies.repository.httpClient.OutboundIdentityClient;
import movies.repository.httpClient.OutboundUserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;

    @NonFinal
    @Value("${spring.jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${spring.jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${spring.jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${spring.outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${spring.outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${spring.outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse outboundAuthenticate(String code) {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("UserInfo {}", userInfo);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name(PredefinedRole.USER_ROLE).build());

        var user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getEmail())
                        .username(userInfo.getName())
                        .firstName(userInfo.getGivenName())
                        .lastName(userInfo.getFamilyName())
                        .roles(roles)
                        .build()));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

            if (!user.isEnabled()) {
                throw new AppException(ErrorCodes.EMAIL_NOT_CONFIRMED);
            }

            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!authenticated) {
                throw new AppException(ErrorCodes.UNAUTHENTICATED);
            }

            var token = generateToken(user);
            return AuthenticationResponse.builder().token(token).build();

        } catch (AppException e) {
            log.warn("Authentication failed - Code: {}, Message: {}", e.getErrorCodes(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            throw new AppException(ErrorCodes.UNCATEGORIZED_EXCEPTION);
        }
    }


    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var email = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).build();
    }

    //login
    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("demo.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    //register, forgot-password
    public String generateEmailToken(User user, String tokenType) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("movie-app")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("type", tokenType)
                .build();

        Payload payload = new Payload(claims.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create verification token", e);
            throw new RuntimeException(e);
        }
    }

    public UserConfirmResponse verifyEmailToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean valid = signedJWT.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            if (!valid) throw new AppException(ErrorCodes.UNAUTHENTICATED);

            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");
            String email = signedJWT.getJWTClaimsSet().getSubject();
            Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

            if (type.equals(PredefinedToken.VERIFICATION_TOKEN)) {
                return handleVerificationToken(user, expiryDate);
            } else if (type.equals(PredefinedToken.PASSWORD_RESET_TOKEN)) {
                return handlePasswordResetToken(user, expiryDate);
            }
            throw new AppException(ErrorCodes.INVALID_TOKEN);

        } catch (Exception e) {
            return UserConfirmResponse.builder()
                    .status(false)
                    .message("Token không hợp lệ hoặc đã hết hạn!")
                    .expiresAt(null)
                    .build();
        }
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());
            boolean valid = signedJWT.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            if (!valid) throw new AppException(ErrorCodes.UNAUTHENTICATED);

            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");
            if (!type.equals(PredefinedToken.PASSWORD_RESET_TOKEN)) {
                throw new AppException(ErrorCodes.INVALID_TOKEN);
            }

            String email = signedJWT.getJWTClaimsSet().getSubject();
            Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (expiryDate.before(new Date())) {
                throw new AppException(ErrorCodes.INVALID_TOKEN);
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResetPasswordResponse.builder()
                    .status(true)
                    .message("Đặt lại mật khẩu thành công!")
                    .expiresAt(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();

        } catch (Exception e) {
            throw new AppException(ErrorCodes.INVALID_TOKEN);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCodes.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCodes.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        return stringJoiner.toString();
    }

    private UserConfirmResponse handleVerificationToken(User user, Date expiryDate) {
        if (user.isEnabled()) {
            return UserConfirmResponse.builder()
                    .status(false)
                    .message("Account already activated.")
                    .build();
        }

        user.setEnabled(true);
        userRepository.save(user);

        return UserConfirmResponse.builder()
                .status(true)
                .message("Xác thực email thành công!")
                .expiresAt(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    private UserConfirmResponse handlePasswordResetToken(User user, Date expiryDate) {
        return UserConfirmResponse.builder()
                .status(true)
                .message("Token hợp lệ. Hãy đặt lại mật khẩu.")
                .expiresAt(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    public String getCurrentUserId() {
        Authentication authentication = getAuthentication();
        return authentication.getName();
    }

    public boolean isAdmin() {
        Authentication authentication = getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + PredefinedRole.ADMIN_ROLE));
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCodes.UNAUTHORIZED);
        }
        return authentication;
    }
}