package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedNotification;
import movies.dto.response.ApiResponse;
import movies.dto.response.Comment.CommentResponse;
import movies.dto.response.NotificationResponse;
import movies.dto.response.PageResponse;
import movies.dto.response.user.UserResponse;
import movies.service.AuthenticationService;
import movies.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notifications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;
    AuthenticationService authenticationService;

    /**
     * WebSocket endpoint for sending notifications.
     * Accessible through STOMP client connected to "/app/notification".
     *
     * @param notification The notification to send
     */
    @MessageMapping("/notification")
    public void processNotification(@Payload NotificationResponse notification) {
        log.info("Received WebSocket notification: {}", notification);

        // If the notification has a specific user, send it to that user's queue
        if (notification.getUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    notification.getUserId(),
                    "/queue/notifications",
                    notification
            );
        } else {
            // Otherwise, broadcast to all users
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        }
    }

    @GetMapping("/me")
    ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        PageResponse<NotificationResponse> notifications = notificationService.getMyNotifications(page, size);
        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .data(notifications)
                .build();
    }

    /**
     * Lấy số lượng thông báo chưa đọc
     */
    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.<Long>builder()
                .data(notificationService.countUnreadNotifications())
                .build();
    }

    /**
     * Đánh dấu thông báo là đã đọc
     */
    @PutMapping("/mark-read")
    public ApiResponse<Void> markAsRead(@RequestBody List<String> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/mark-all-read")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllNotificationsAsRead();
        return ApiResponse.<Void>builder().build();
    }

    /**
     * Xóa thông báo
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<Void>builder().build();
    }
}
