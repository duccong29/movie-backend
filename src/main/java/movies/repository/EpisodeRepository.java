package movies.repository;

import movies.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, String> {
    List<Episode> findBySeasonIdOrderByEpisodeNumber(String seasonId);
    Optional<Episode> findBySeasonIdAndEpisodeNumber(String seasonId, Integer episodeNumber);
}
