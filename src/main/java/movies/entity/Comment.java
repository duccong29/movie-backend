
package movies.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String text;

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
    LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        // Ensure only one content type is set
        int contentTypeCount = 0;
        if (movie != null) contentTypeCount++;
        if (series != null) contentTypeCount++;
        if (episode != null) contentTypeCount++;

        if (contentTypeCount != 1) {
            throw new IllegalStateException("A comment must be associated with exactly one content type (movie, series, or episode)");
        }
    }
}
