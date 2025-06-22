package movies.repository;

import movies.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, String> {

    // Find by user and content
    Optional<WatchHistory> findByUserIdAndMovieId(String userId, String movieId);
    Optional<WatchHistory> findByUserIdAndSeriesId(String userId, String seriesId);
    Optional<WatchHistory> findByUserIdAndEpisodeId(String userId, String episodeId);


    // Find watch history by user and movie
    Optional<WatchHistory> findByUserAndMovie(User user, Movie movie);

    // Find watch history by user and episode
    Optional<WatchHistory> findByUserAndEpisode(User user, Episode episode);

    // Find watch history by user and series
    List<WatchHistory> findByUserAndSeries(User user, Series series);

    // Find all watch histories by user
    List<WatchHistory> findByUserOrderByLastWatchedAtDesc(User user);

    // Count watches for a movie
    long countByMovie(Movie movie);

    // Count watches for an episode
    long countByEpisode(Episode episode);

    // Count watches for a user
    long countByUser(User user);
    long countByUserId(String userId);
    // Delete by user and movie
    void deleteByUserIdAndMovieId(String userId, String movieId);
    void deleteByUserIdAndSeriesId(String userId, String seriesId);
    // Delete by user and episode
    void deleteByUserIdAndEpisodeId(String userId, String episodeId);

    // Delete by user and series
    void deleteByUserAndSeries(User user, Series series);

    boolean existsByIdAndUserId(String id, String userId);

    List<WatchHistory> findByUserIdOrderByLastWatchedAtDesc(String userId);

}
