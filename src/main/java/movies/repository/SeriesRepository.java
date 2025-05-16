package movies.repository;

import movies.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, String> {
    boolean existsByTitleIgnoreCase(String title);
    Optional<Series> findByTitleIgnoreCase(String title);

    @Query("SELECT DISTINCT s FROM Series s LEFT JOIN s.genres g WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Series> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT s FROM Series s JOIN s.genres g WHERE g.id = :genreId")
    Page<Series> findByGenreId(@Param("genreId") String genreId, Pageable pageable);

    List<Series> findTop10ByOrderByAverageRatingDesc();

    List<Series> findTop10ByOrderByCreatedAtDesc();
}
