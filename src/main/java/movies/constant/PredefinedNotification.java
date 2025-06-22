package movies.constant;

public class PredefinedNotification {
    public static final String NEW_MOVIE = "MOVIES";
    public static final String NEW_SERIES = "SERIES";
    public static final String NEW_EPISODE = "EPISODE";

    // Loại thông báo
    public static final String TYPE_NEW_CONTENT = "NEW_CONTENT";
    public static final String TYPE_COMMENT_REPLY = "COMMENT_REPLY";
    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_COMMENT = "COMMENT";
    public static final String TYPE_REVIEW = "REVIEW";

    // Template tiêu đề
    public static final String TITLE_NEW_MOVIE = "New Movie Available";
    public static final String TITLE_NEW_SERIES = "New Series Available";
    public static final String TITLE_NEW_EPISODE = "New Episode Available";
    public static final String TITLE_COMMENT_REPLY = "New Reply to Your Comment";
    public static final String TITLE_NEW_COMMENT = "New Comment on %s";
    public static final String TITLE_NEW_REVIEW = "New Review on %s";
    public static final String TITLE_EMAIL_FAILURE = "Email Delivery Failed";

    // Template nội dung
    public static final String CONTENT_NEW_MOVIE = "A new movie '%s' has been added. Check it out now!";
    public static final String CONTENT_NEW_SERIES = "A new series '%s' has been added. Check it out now!";
    public static final String CONTENT_NEW_EPISODE = "A new episode (S%sE%s) of '%s' is now available. Watch it now!";
    public static final String CONTENT_COMMENT_REPLY = "Someone replied to your comment. Check it out now!";
    public static final String CONTENT_COMMENT = "User %s commented on %s: %s";
    public static final String CONTENT_REVIEW = "User %s rated %s %d stars%s";
    public static final String CONTENT_EMAIL_FAILURE = "Original content: %s";

    // Template URL
    public static final String URL_MOVIE = "/movies/%s";
    public static final String URL_SERIES = "/series/%s";
    public static final String URL_EPISODE = "/series/%s/season/%s/episode/%s";
    public static final String URL_COMMENT_SUFFIX = "#comment-%s";
    public static final String URL_REVIEW_SUFFIX = "#review-%s";

    // Phương thức builder URL
    public static String buildMovieUrl(String id) {
        return String.format(URL_MOVIE, id);
    }

    public static String buildSeriesUrl(String id) {
        return String.format(URL_SERIES, id);
    }

    public static String buildEpisodeUrl(String seriesId, String seasonId, String episodeId) {
        return String.format(URL_EPISODE, seriesId, seasonId, episodeId);
    }

    public static String buildCommentUrl(String baseUrl, String commentId) {
        return baseUrl + String.format(URL_COMMENT_SUFFIX, commentId);
    }

    public static String buildReviewUrl(String baseUrl, String reviewId) {
        return baseUrl + String.format(URL_REVIEW_SUFFIX, reviewId);
    }

    private PredefinedNotification() {}
}
