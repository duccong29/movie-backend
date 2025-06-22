package movies.constant;

import java.util.Arrays;
import java.util.List;

public class PredefinedImages {
    public static final String MOVIE_ENTITY_TYPE = "MOVIE";
    public static final String USER_ENTITY_TYPE = "USER";
    public static final String EPISODE_ENTITY_TYPE = "EPISODE";
    public static final String SERIES_ENTITY_TYPE = "SERIES";
    public static final String SEASON_ENTITY_TYPE = "SEASON";


    public static final String POSTER = "poster";
    public static final String BACKDROP = "backdrop";
    public static final String THUMBNAIL = "thumbnail";
    public static final String PROFILE = "profile";
    public static final String AVATAR = "avatar";
    public static final String LOGO = "logo";
    public static final String STILL = "still";
    public static final String EPISODE = "episode";
    public static final String OTHER = "other";

    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    public static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public static final String LOCAL_UPLOAD_DIR = "uploads/images";
    public static final String LOCAL_ACCESS_URL_PREFIX = "/uploads/images/";

    private PredefinedImages() {}
}
