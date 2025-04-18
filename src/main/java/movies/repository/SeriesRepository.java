package movies.repository;

import movies.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, String> {
    boolean existsByTitleIgnoreCase(String title);
    Optional<Series> findByTitleIgnoreCase(String title);
}
