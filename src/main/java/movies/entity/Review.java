package movies.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String comment;

    @Min(1)
    @Max(5)
    Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    Series series;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void validateContentType() {
        // Ensure only one content type is set
        if ((movie == null && series == null) || (movie != null && series != null)) {
            throw new IllegalStateException("A review must be associated with exactly one content type (movie or series)");
        }

        // Update the average rating
        if (movie != null) {
            movie.updateAverageRating();
        } else if (series != null) {
            series.updateAverageRating();
        }
    }
}