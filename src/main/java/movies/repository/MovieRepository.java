package movies.repository;

import movies.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    boolean existsByTitleIgnoreCase(String title);

    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN m.genres g WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Movie> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Page<Movie> findByGenreId(@Param("genreId") String genreId, Pageable pageable);

    List<Movie> findTop10ByOrderByAverageRatingDesc();

    List<Movie> findTop10ByOrderByCreatedAtDesc();
}
