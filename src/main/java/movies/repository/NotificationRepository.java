package movies.repository;

import feign.Param;
import movies.entity.Movie;
import movies.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    Page<Notification> findByUserId(String userId, Pageable pageable);

    long countByUserIdAndReadStatus(String userId, boolean readStatus);

    List<Notification> findByIdInAndUserId(List<String> ids, String userId);

    Optional<Notification> findByIdAndUserId(String id, String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.readStatus = true WHERE n.userId = :userId")
    void markAllAsRead(@Param("userId") String userId);
}
