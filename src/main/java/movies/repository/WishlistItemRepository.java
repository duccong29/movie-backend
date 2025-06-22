package movies.repository;

import movies.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {
    Page<WishlistItem> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<WishlistItem> findByUserIdAndMovieId(String userId, String movieId);

    Optional<WishlistItem> findByUserIdAndSeriesId(String userId, String seriesId);

    boolean existsByUserIdAndMovieId(String userId, String movieId);

    boolean existsByUserIdAndSeriesId(String userId, String seriesId);

    void deleteByUserIdAndMovieId(String userId, String movieId);

    void deleteByUserIdAndSeriesId(String userId, String seriesId);

    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.movie.id = :movieId")
    Long countWishlistsForMovie(@Param("movieId") String movieId);

    @Query("SELECT COUNT(w) FROM WishlistItem w WHERE w.series.id = :seriesId")
    Long countWishlistsForSeries(@Param("seriesId") String seriesId);
}
