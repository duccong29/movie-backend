package movies.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
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
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String filePath;
    String hlsPath;
//    String status;

    String fileName;
    String originalFileName;
    String fileType;
    Long fileSize;
    String cloudinaryPublicId;
    String cloudinaryUrl;
    String localUrl;
    Boolean isStoredLocally;
    Boolean isStoredInCloudinary;

    //    @ManyToOne(fetch = FetchType.LAZY)
//    Movie movie;
    @OneToOne
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    Episode episode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
