package movies.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedNotification;
import movies.dto.request.Notification.*;
import movies.dto.response.ContentContextResponse;
import movies.dto.response.NotificationResponse;
import movies.dto.response.PageResponse;
import movies.entity.*;
import movies.mapper.NotificationMapper;
import movies.repository.NotificationRepository;
import movies.utils.ContentContextExtractor;
import movies.utils.NotificationEventPublisher;
import movies.utils.NotificationRecipientResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    @NonFinal
    @Value("${spring.mail.username}")
    protected String fromEmail;

    JavaMailSender mailSender;
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    AuthenticationService authenticationService;
    ContentContextExtractor contentContextExtractor;
    NotificationEventPublisher notificationEventPublisher;
    NotificationRecipientResolver notificationRecipientResolver;

    // =============== PUBLIC METHODS ===============

    public void sendNewMovieNotification(NewMovieNotificationRequest request) {
        NotificationResponse notification = NotificationResponse.builder()
                .title(PredefinedNotification.TITLE_NEW_MOVIE)
                .content(String.format(PredefinedNotification.CONTENT_NEW_MOVIE, request.getTitle()))
                .notificationType(PredefinedNotification.TYPE_NEW_CONTENT)
                .userId(request.getTargetUserId())
                .targetUrl(PredefinedNotification.buildMovieUrl(request.getMovieId()))
                .build();

        notificationEventPublisher.publishNotification(notification);
    }

    public void sendNewSeriesNotification(NewSeriesNotificationRequest request) {
        NotificationResponse notification = NotificationResponse.builder()
                .title(PredefinedNotification.TITLE_NEW_SERIES)
                .content(String.format(PredefinedNotification.CONTENT_NEW_SERIES, request.getTitle()))
                .notificationType(PredefinedNotification.TYPE_NEW_CONTENT)
                .userId(request.getTargetUserId())
                .targetUrl(PredefinedNotification.buildSeriesUrl(request.getSeriesId()))
                .build();

        notificationEventPublisher.publishNotification(notification);
    }

    public void sendNewEpisodeNotification(NewEpisodeNotificationRequest request) {
        String content = String.format(PredefinedNotification.CONTENT_NEW_EPISODE,
                request.getSeasonId(), request.getEpisodeId(), request.getSeriesTitle());

        NotificationResponse notification = NotificationResponse.builder()
                .title(PredefinedNotification.TITLE_NEW_EPISODE)
                .content(content)
                .notificationType(PredefinedNotification.TYPE_NEW_CONTENT)
                .userId(request.getTargetUserId())
                .targetUrl(PredefinedNotification.buildEpisodeUrl(
                        request.getSeriesId(), request.getSeasonId(), request.getEpisodeId()))
                .build();

        notificationEventPublisher.publishNotification(notification);
    }

    public void sendSystemNotification(SystemNotificationRequest request) {
        NotificationResponse notification = NotificationResponse.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .notificationType(PredefinedNotification.TYPE_SYSTEM)
                .userId(request.getTargetUserId())
                .build();

        notificationEventPublisher.publishNotification(notification);
    }

    public void sendCommentReplyNotification(CommentReplyNotificationRequest request) {
        String targetUrl = contentContextExtractor.buildCommentUrl(
                request.getContentType(), request.getContentId(), request.getCommentId());

        NotificationResponse notification = NotificationResponse.builder()
                .title(PredefinedNotification.TITLE_COMMENT_REPLY)
                .content(PredefinedNotification.CONTENT_COMMENT_REPLY)
                .notificationType(PredefinedNotification.TYPE_COMMENT_REPLY)
                .userId(request.getTargetUserId())
                .targetUrl(targetUrl)
                .build();

        notificationEventPublisher.publishNotification(notification);
    }

    public void notifyNewComment(Comment comment) {
        ContentContextResponse context = contentContextExtractor.extractFromComment(comment);
        if (context == null) return;

        String notificationTitle = String.format(
                PredefinedNotification.TITLE_NEW_COMMENT,
                StringUtils.capitalize(context.getType())
        );

        String content = String.format(
                PredefinedNotification.CONTENT_COMMENT,
                comment.getUser().getUsername(),
                context.getTitle(),
                getContentPreview(comment.getText())
        );

        List<String> recipients = notificationRecipientResolver.resolveCommentRecipients(comment);

        recipients.forEach(userId -> {
            NotificationResponse notification = NotificationResponse.builder()
                    .title(notificationTitle)
                    .content(content)
                    .notificationType(PredefinedNotification.TYPE_COMMENT)
                    .userId(userId)
                    .targetUrl(PredefinedNotification.buildCommentUrl(context.getBaseUrl(), comment.getId()))
                    .build();

            notificationEventPublisher.publishNotification(notification);
        });

        log.info("Comment notifications sent for {}: {}", context.getType(), context.getTitle());
    }

        public void notifyNewReply(Comment reply) {
            if (reply.getParentComment() == null) return;

            Comment parent = reply.getParentComment();
            if (parent.getUser().getId().equals(reply.getUser().getId())) return;

            ContentContextResponse context = contentContextExtractor.extractFromComment(parent);
            if (context == null) return;

            CommentReplyNotificationRequest req = CommentReplyNotificationRequest.builder()
                    .commentId(parent.getId())
                    .contentId(context.getContentId())
                    .contentType(context.getType())
                    .targetUserId(parent.getUser().getId())
                    .build();

            sendCommentReplyNotification(req);
        }


    public void notifyNewReview(Review review) {
        ContentContextResponse context = contentContextExtractor.extractFromReview(review);
        if (context == null) {
            log.warn("Failed to extract context for review: {}", review.getId());
            return;
        }

        String notificationTitle = String.format(
                PredefinedNotification.TITLE_NEW_REVIEW,
                StringUtils.capitalize(context.getType())
        );

        String content = String.format(
                PredefinedNotification.CONTENT_REVIEW,
                review.getUser().getUsername(),
                context.getTitle(),
                getContentPreview(review.getComment())
        );

        List<String> recipients = notificationRecipientResolver.resolveReviewRecipients(review);

        recipients.forEach(userId -> {
            NotificationResponse notification = NotificationResponse.builder()
                    .title(notificationTitle)
                    .content(content)
                    .notificationType(PredefinedNotification.TYPE_REVIEW)
                    .userId(userId)
                    .targetUrl(PredefinedNotification.buildReviewUrl(context.getBaseUrl(), review.getId()))
                    .build();

            notificationEventPublisher.publishNotification(notification);
        });

        log.info("Sent {} review notifications for {}: {}",
                recipients.size(), context.getType(), context.getTitle());
    }

//    public void sendNotificationEmail(String recipientEmail, String subject, String body) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(recipientEmail);
//            message.setSubject(subject);
//            message.setText(body);
//            mailSender.send(message);
//            log.info("Email notification sent to {}", recipientEmail);
//        } catch (Exception e) {
//            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage());
//            sendEmailFallbackNotification(subject, body);
//        }
//    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(int page, int size) {
        String userId = authenticationService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserId(userId, pageable);

        return PageResponse.<NotificationResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .data(notificationPage.map(notificationMapper::toNotificationResponse).getContent())
                .build();
    }

    @Transactional(readOnly = true)
    public long countUnreadNotifications() {
        return notificationRepository.countByUserIdAndReadStatus(
                authenticationService.getCurrentUserId(), false
        );
    }

    @Transactional
    public void markNotificationsAsRead(List<String> notificationIds) {
        String userId = authenticationService.getCurrentUserId();
        List<Notification> notifications = notificationRepository
                .findByIdInAndUserId(notificationIds, userId);

        notifications.forEach(notification ->
                notification.setReadStatus(true));

        notificationRepository.saveAll(notifications);

    }

    @Transactional
    public void markAllNotificationsAsRead() {
        notificationRepository.markAllAsRead(authenticationService.getCurrentUserId());
    }

    @Transactional
    public void deleteNotification(String id) {
        String userId = authenticationService.getCurrentUserId();
        Notification notification = notificationRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found or access denied"));
        notificationRepository.delete(notification);
    }

    // =============== HELPER METHODS ===============


    private String getContentPreview(String text) {
        return StringUtils.abbreviate(text, 100);
    }

}