package movies.utils;

import lombok.extern.slf4j.Slf4j;
import movies.entity.Comment;
import movies.entity.Review;
import movies.entity.Series;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class NotificationRecipientResolver {

    public List<String> resolveCommentRecipients(Comment comment) {
        Set<String> recipients = new HashSet<>();

        // Add content owner
        addContentOwner(comment, recipients);

        // Add parent comment authors in thread
        addParentCommentAuthors(comment, recipients);

        // Remove comment author to avoid self-notification
        recipients.remove(comment.getUser().getId());

        List<String> result = new ArrayList<>(recipients);
        log.debug("Resolved {} recipients for comment notification", result.size());
        return result;
    }

    public List<String> resolveReviewRecipients(Review review) {
        Set<String> recipients = new HashSet<>();

        if (review.getMovie() != null && review.getMovie().getUser() != null) {
            recipients.add(review.getMovie().getUser().getId());
        } else if (review.getSeries() != null && review.getSeries().getUser() != null) {
            recipients.add(review.getSeries().getUser().getId());
        }

        // Remove review author to avoid self-notification
        recipients.remove(review.getUser().getId());

        List<String> result = new ArrayList<>(recipients);
        log.debug("Resolved {} recipients for review notification", result.size());
        return result;
    }

    private void addContentOwner(Comment comment, Set<String> recipients) {
        if (comment.getMovie() != null && comment.getMovie().getUser() != null) {
            recipients.add(comment.getMovie().getUser().getId());
        } else if (comment.getSeries() != null && comment.getSeries().getUser() != null) {
            recipients.add(comment.getSeries().getUser().getId());
        } else if (comment.getEpisode() != null) {
            Series series = comment.getEpisode().getSeason().getSeries();
            if (series != null && series.getUser() != null) {
                recipients.add(series.getUser().getId());
            }
        }
    }

    private void addParentCommentAuthors(Comment comment, Set<String> recipients) {
        Comment current = comment;
        while (current.getParentComment() != null) {
            current = current.getParentComment();
            if (current.getUser() != null) {
                recipients.add(current.getUser().getId());
            }
        }
    }
}
