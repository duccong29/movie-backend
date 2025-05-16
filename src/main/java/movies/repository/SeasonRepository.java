package movies.repository;

import movies.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, String> {
    Optional<Season> findBySeriesIdAndSeasonNumber(String seriesId, Integer seasonNumber);

    List<Season> findBySeriesIdOrderBySeasonNumber(String seriesId);
}
