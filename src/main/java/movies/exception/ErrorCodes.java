package movies.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // ===== Common =====
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    MOVIE_SAVE_FAILED(9998, "movie save failed", HttpStatus.INTERNAL_SERVER_ERROR),


    // ===== Existence Errors =====
    USER_EXISTED(2000, "User existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(2001, "Email already existed", HttpStatus.BAD_REQUEST),
    GENRE_EXISTED(2002, "Genre existed", HttpStatus.BAD_REQUEST),
    MOVIE_EXISTED(2003, "Movie existed", HttpStatus.BAD_REQUEST),
    VIDEO_EXISTED(2004, "Video existed", HttpStatus.BAD_REQUEST),
    SERIES_EXISTED(2005, "Series existed", HttpStatus.BAD_REQUEST),
    SEASON_EXISTED(2006, "Season existed", HttpStatus.BAD_REQUEST),
    EPISODE_EXISTED(2007, "Episode existed", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(2100, "User not existed", HttpStatus.NOT_FOUND),
    EMAIL_NOT_EXISTED(2101, "Email not existed", HttpStatus.NOT_FOUND),
    GENRE_NOT_EXISTED(2102, "Genre not existed", HttpStatus.NOT_FOUND),
    MOVIE_NOT_EXISTED(2103, "Movie not existed", HttpStatus.NOT_FOUND),
    VIDEO_NOT_EXISTED(2104, "Video not existed", HttpStatus.NOT_FOUND),
    SERIES_NOT_EXISTED(2105, "Series not existed", HttpStatus.NOT_FOUND),
    SEASON_NOT_EXISTED(2106, "Season not existed", HttpStatus.NOT_FOUND),
    EPISODE_NOT_EXISTED(2107, "Episode not existed", HttpStatus.NOT_FOUND),
    IMAGE_NOT_EXISTED(2108, "images not existed", HttpStatus.NOT_FOUND),

    // ===== Auth & Token =====
    INVALID_KEY(3000, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(3001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(3002, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(3003, "Invalid token", HttpStatus.BAD_REQUEST),
    EXPIRED_RESET_TOKEN(3004, "Expired reset token", HttpStatus.NOT_FOUND),
    INVALID_TOKEN_TYPE(3005, "Invalid token type", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_CONFIRMED(3006, "You have not verified your email", HttpStatus.BAD_REQUEST),

    // ===== Validation & Required =====
    USERNAME_INVALID(4000, "Username must be at least 4 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(4001, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    INVALID_DOB(4002, "Date of Birth must be in the past", HttpStatus.BAD_REQUEST),
    GENRE_INVALID(4003, "Genre name must be between 2 and 50 characters", HttpStatus.BAD_REQUEST),
    GENRE_REQUIRED(4004, "Genre name is required", HttpStatus.BAD_REQUEST),
    TITLE_INVALID(4005, "Title must be less than 255 characters", HttpStatus.BAD_REQUEST),
    TITLE_REQUIRED(4006, "Title is required", HttpStatus.BAD_REQUEST),
    SEASON_INVALID(4007, "Season number must be at least 1", HttpStatus.BAD_REQUEST),
    SERIES_REQUIRED(4008, "Series ID is required", HttpStatus.BAD_REQUEST),
    SEASON_REQUIRED(4009, "Season ID is required", HttpStatus.BAD_REQUEST),
    EPISODE_REQUIRED(4010, "Episode number is required", HttpStatus.BAD_REQUEST),
    EPISODE_INVALID(4011, "Episode number must be at least 1", HttpStatus.BAD_REQUEST),

    // ===== Video =====
    VIDEO_INVALID_OWNER(5000, "Video invalid owner", HttpStatus.BAD_REQUEST),
    VIDEO_PROCESSING_ERROR(5001, "Error in processing video", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_VIDEO_FILE(5002, "Invalid image file", HttpStatus.BAD_REQUEST),
    INVALID_VIDEO_SIZE(5003, "Image size exceeds the maximum allowed limit", HttpStatus.BAD_REQUEST),
    INVALID_VIDEO_FORMAT(5004, "Unsupported image format", HttpStatus.BAD_REQUEST),
    VIDEO_PROCESSING_FAILED(5005, "Failed to process image file", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== Search =====
    MOVIES_NOT_FOUND_BY_GENRE(6000, "No movies found for the given genre", HttpStatus.NOT_FOUND),
    SERIES_NOT_FOUND_BY_GENRE(6001, "No series found for the given genre", HttpStatus.NOT_FOUND),
    MOVIES_NOT_FOUND_BY_QUERY(6002, "No movies found matching the query", HttpStatus.NOT_FOUND),
    SERIES_NOT_FOUND_BY_QUERY(6003, "No series found matching the query", HttpStatus.NOT_FOUND),

    // ===== Images =====
    IMAGE_UPLOAD_FAILED(7000, "Failed to upload image to both Cloudinary and local storage", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_FILE_EMPTY(7001, "File cannot be empty", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED(7002, "File size exceeds the maximum allowed size of 5MB", HttpStatus.PAYLOAD_TOO_LARGE),
    IMAGE_FILE_TYPE_NOT_SUPPORTED(7003, "File type not supported. Allowed types: JPEG, PNG, GIF, WEBP", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    IMAGE_STORAGE_DIRECTORY_CREATION_FAILED(7004, "Failed to create directory for storing image", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_PROCESSING_FAILED(7005, "Failed to process image file", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_NOT_FOUND(7006, "Image not found", HttpStatus.NOT_FOUND),
    INVALID_IMAGE_FILE(7007, "Invalid image file", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_SIZE(7008, "Image size exceeds the maximum allowed limit", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_FORMAT(7009, "Unsupported image format", HttpStatus.BAD_REQUEST),



    ;
    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}

