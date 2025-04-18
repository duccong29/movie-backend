package movies.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Integer episodeNumber;
    String title;
    Integer duration;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Video> videos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    Season season;
}
