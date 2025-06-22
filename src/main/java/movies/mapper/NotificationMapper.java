package movies.mapper;

import movies.dto.response.NotificationResponse;
import movies.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toNotificationResponse(Notification notification);

    Notification toNotification(NotificationResponse response);
}
