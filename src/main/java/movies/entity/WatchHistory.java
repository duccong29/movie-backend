package movies.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Integer watchTimeSeconds;

    Integer totalDurationSeconds;

    Double watchProgress;

    LocalDateTime lastWatchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    Episode episode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Trước khi lưu, tính toán tiến độ xem và kiểm tra xem chỉ có 1 content type (movie hoặc episode) được set.
     * Với phim bộ, ta chỉ bắt buộc phải có episode, còn series chỉ là thông tin bổ trợ nên không tính.
     */
    @PrePersist
    @PreUpdate
    public void beforeSave() {
        calculateProgress();

        int contentTypeCount = (movie != null ? 1 : 0) + (episode != null ? 1 : 0);
        if (contentTypeCount != 1) {
            throw new IllegalStateException("A watch history item must be associated with exactly one content type (movie or episode)");
        }
    }

    /**
     * Tính toán watchProgress dựa trên watchTimeSeconds và totalDurationSeconds.
     */
    public void calculateProgress() {
        if (watchTimeSeconds != null && totalDurationSeconds != null && totalDurationSeconds > 0) {
            watchProgress = (double) watchTimeSeconds / totalDurationSeconds;
            if (watchProgress > 1.0) {
                watchProgress = 1.0;
            }
        }
    }
}