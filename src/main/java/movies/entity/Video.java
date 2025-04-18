package movies.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String filePath;
    String hlsPath;

    @CreationTimestamp
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    Episode episode;
}
