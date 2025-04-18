package movies.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 4 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Date of Birth must be in the past", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_CONFIRMED(1009, "You have not identified your email.",  HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1010, "Invalid token.", HttpStatus.BAD_REQUEST),
    EXPIRED_RESET_TOKEN(1011,"Expired reset token.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1012, "Email already exists.",  HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(1013, "username already exists.",  HttpStatus.BAD_REQUEST),
    GENRE_ALREADY_EXISTS(1014, "Genre already exists.",  HttpStatus.BAD_REQUEST),
    GENRE_NOT_EXISTED(1015, "Genre not existed", HttpStatus.NOT_FOUND),
    MOVIE_EXISTED(1016, "Movie existed", HttpStatus.BAD_REQUEST),
    GENRES_NOT_FOUND(1016, "Genres not found with ids:", HttpStatus.NOT_FOUND),
    MOVIE_NOT_FOUND(1017, "Movie not found", HttpStatus.NOT_FOUND),
    VIDEO_NOT_FOUND(1018, "Video not found", HttpStatus.NOT_FOUND),
    VIDEO_EXISTED(1019, "Video existed", HttpStatus.BAD_REQUEST),
    VIDEO_PROCESSING_ERROR(1020, "Error in processing video", HttpStatus.INTERNAL_SERVER_ERROR),
    SERIES_ALREADY_EXISTS(1021, "Series already exists.",  HttpStatus.BAD_REQUEST),
    SERIES_NOT_EXISTED(1022, "Series not existed", HttpStatus.NOT_FOUND),
    SERIES_NOT_FOUND(1023, "Series not found", HttpStatus.NOT_FOUND),
    SEASON_NOT_FOUND(1024, "Season not found", HttpStatus.NOT_FOUND),
    SEASON_ALREADY_EXISTS(1025, "Series already exists.",  HttpStatus.BAD_REQUEST),
    VIDEO_INVALID_OWNER(1025, "Video invalid owner.",  HttpStatus.BAD_REQUEST),
    EPISODE_NOT_FOUND(1026, "Episode not found", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
