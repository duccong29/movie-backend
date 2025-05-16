package movies.constant;

import java.util.Arrays;
import java.util.List;

public class PredefinedVideos {
    public static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "video/mp4",
            "video/quicktime",
            "video/x-msvideo"
    );

    public static final long MAX_FILE_SIZE = 1024 * 1024 * 1024;


    private PredefinedVideos() {}
}
