package movies.utils;

import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedNotification;
import movies.dto.response.ContentContextResponse;
import movies.entity.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContentContextExtractor {
    public ContentContextResponse extractFromComment(Comment comment) {
        if (comment.getMovie() != null) {
            return createMovieContext(comment.getMovie());
        } else if (comment.getSeries() != null) {
            return createSeriesContext(comment.getSeries());
        } else if (comment.getEpisode() != null) {
            return createEpisodeContext(comment.getEpisode());
        }

        log.warn("Unable to extract content context from comment: {}", comment.getId());
        return null;
    }

    public ContentContextResponse extractFromReview(Review review) {
        if (review.getMovie() != null) {
            return createMovieContext(review.getMovie());
        } else if (review.getSeries() != null) {
            return createSeriesContext(review.getSeries());
        }

        log.warn("Unable to extract content context from review: {}", review.getId());
        return null;
    }

    public String buildCommentUrl(String contentType, String contentId, String commentId) {
        switch (contentType.toLowerCase()) {
            case "movie":
                return PredefinedNotification.buildCommentUrl(
                        PredefinedNotification.buildMovieUrl(contentId), commentId);
            case "series":
                return PredefinedNotification.buildCommentUrl(
                        PredefinedNotification.buildSeriesUrl(contentId), commentId);
            case "episode":
                String[] parts = contentId.split("/");
                if (parts.length == 3) {
                    return PredefinedNotification.buildCommentUrl(
                            PredefinedNotification.buildEpisodeUrl(
                                    parts[0], parts[1], parts[2]),
                            commentId);
                }
                break;
        }
        return "/content/" + contentId + "#comment-" + commentId;
    }

    private ContentContextResponse createMovieContext(Movie movie) {
        return ContentContextResponse.builder()
                .type("movie")
                .title(movie.getTitle())
                .contentId(movie.getId())
                .baseUrl(PredefinedNotification.buildMovieUrl(movie.getId()))
                .build();
    }

    private ContentContextResponse createSeriesContext(Series series) {
        return ContentContextResponse.builder()
                .type("series")
                .title(series.getTitle())
                .contentId(series.getId())
                .baseUrl(PredefinedNotification.buildSeriesUrl(series.getId()))
                .build();
    }

    private ContentContextResponse createEpisodeContext(Episode episode) {
        Series series = episode.getSeason().getSeries();
        String contentId = series.getId() + "/" +
                episode.getSeason().getId() + "/" +
                episode.getId();

        return ContentContextResponse.builder()
                .type("episode")
                .title(episode.getTitle())
                .contentId(contentId)
                .baseUrl(PredefinedNotification.buildEpisodeUrl(
                        series.getId(),
                        episode.getSeason().getId(),
                        episode.getId()))
                .build();
    }
}
