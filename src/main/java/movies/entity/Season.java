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
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Integer seasonNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    Series series;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Episode> episodes = new ArrayList<>();
}
