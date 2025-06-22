package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.watchHistory.WatchHistoryRequest;
import movies.dto.response.watchHistory.WatchHistoryResponse;
import movies.entity.*;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.WatchHistoryMapper;
import movies.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryService {
    WatchHistoryRepository watchHistoryRepository;
    MovieRepository movieRepository;
    EpisodeRepository episodeRepository;
    WatchHistoryMapper watchHistoryMapper;
    AuthenticationService authenticationService;
    UserService userService;

    /**
     * Update watch history for a movie
     */
    @Transactional
    public WatchHistoryResponse updateMovieWatchHistory(WatchHistoryRequest request) {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        Optional<WatchHistory> existingHistory = watchHistoryRepository.findByUserIdAndMovieId(currentUserId, request.getMovieId());
        WatchHistory watchHistory;

        if (existingHistory.isPresent()) {
            watchHistory = existingHistory.get();
            updateWatchHistoryFields(watchHistory, request);
        } else {
            watchHistory = WatchHistory.builder()
                    .user(currentUser)
                    .movie(movie)
                    .watchTimeSeconds(request.getWatchTimeSeconds())
                    .totalDurationSeconds(request.getTotalDurationSeconds())
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
        }

        watchHistory = watchHistoryRepository.save(watchHistory);

        return watchHistoryMapper.toWatchHistoryResponse(watchHistory);
    }

    /**
     * Update watch history for an episode
     */
    @Transactional
    public WatchHistoryResponse updateEpisodeWatchHistory(WatchHistoryRequest request) {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED));

        Series series = null;
        if (episode.getSeason() != null) {
            series = episode.getSeason().getSeries();
        }

        Optional<WatchHistory> existingHistory = watchHistoryRepository.findByUserIdAndEpisodeId(currentUserId, request.getEpisodeId());
        WatchHistory watchHistory;
        if (existingHistory.isPresent()) {
            watchHistory = existingHistory.get();
            updateWatchHistoryFields(watchHistory, request);
        } else {
            watchHistory = WatchHistory.builder()
                    .user(currentUser)
                    .episode(episode)
                    .series(series)
                    .watchTimeSeconds(request.getWatchTimeSeconds())
                    .totalDurationSeconds(request.getTotalDurationSeconds())
                    .lastWatchedAt(LocalDateTime.now())
                    .build();
        }

        watchHistory = watchHistoryRepository.save(watchHistory);

        return watchHistoryMapper.toWatchHistoryResponse(watchHistory);
    }

    /**
     * Get watch history by user
     */
    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getUserWatchHistory() {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);
        log.info("Getting watch history for user: {}", currentUserId);
        List<WatchHistory> watchHistories = watchHistoryRepository.findByUserOrderByLastWatchedAtDesc(currentUser);

        return watchHistories.stream()
                .map(watchHistoryMapper::toWatchHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get watch history by user and movie
     */
    @Transactional(readOnly = true)
    public Optional<WatchHistoryResponse> getMovieWatchHistory(String movieId) {
        String currentUserId = authenticationService.getCurrentUserId();
        return watchHistoryRepository.findByUserIdAndMovieId(currentUserId, movieId)
                .map(watchHistoryMapper::toWatchHistoryResponse);
    }

    /**
     * Get watch history by user and episode
     */
    @Transactional(readOnly = true)
    public Optional<WatchHistoryResponse> getEpisodeWatchHistory(String episodeId) {
        String currentUserId = authenticationService.getCurrentUserId();

        return watchHistoryRepository.findByUserIdAndEpisodeId(currentUserId, episodeId)
                .map(watchHistoryMapper::toWatchHistoryResponse);
    }

    /**
     * Get watch history by user and series
     */
    @Transactional(readOnly = true)
    public List<WatchHistoryResponse> getSeriesWatchHistory( String seriesId) {
        String currentUserId = authenticationService.getCurrentUserId();
        return watchHistoryRepository.findByUserIdAndSeriesId(currentUserId, seriesId)
                .stream()
                .map(watchHistoryMapper::toWatchHistoryResponse)
                .toList();
    }

    /**
     * Count watches for a movie
     */
    @Transactional(readOnly = true)
    public long countMovieWatches(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));
        return watchHistoryRepository.countByMovie(movie);
    }

    /**
     * Count watches for an episode
     */
    @Transactional(readOnly = true)
    public long countEpisodeWatches(String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED));
        return watchHistoryRepository.countByEpisode(episode);
    }

    /**
     * Count watches for a user
     */
    @Transactional(readOnly = true)
    public long countUserWatches() {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        return watchHistoryRepository.countByUser(currentUser);
    }

    /**
     * Delete movie watch history
     */
    @Transactional
    public void deleteMovieWatchHistory(String movieId) {
        String currentUserId = authenticationService.getCurrentUserId();
            watchHistoryRepository.deleteByUserIdAndMovieId(currentUserId, movieId);
    }

    /**
     * Delete episode watch history
     */
    @Transactional
    public void deleteEpisodeWatchHistory( String episodeId) {
        String currentUserId = authenticationService.getCurrentUserId();
        watchHistoryRepository.deleteByUserIdAndEpisodeId(currentUserId, episodeId);
    }

    /**
     * Delete watch history by ID
     */

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @watchHistoryService.isOwner(#historyId)")
    public void deleteWatchHistory(String historyId) {
        WatchHistory history = watchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new AppException(ErrorCodes.WATCH_HISTORY_NOT_EXISTED));

        watchHistoryRepository.delete(history);
        log.info("Deleted watch history {}", historyId);
    }

    public boolean isOwner(String historyId) {
        String userId = authenticationService.getCurrentUserId();
        return watchHistoryRepository.existsByIdAndUserId(historyId, userId);
    }


    private void updateWatchHistoryFields(WatchHistory watchHistory, WatchHistoryRequest request) {
        if (request.getWatchTimeSeconds() != null) {
            watchHistory.setWatchTimeSeconds(request.getWatchTimeSeconds());
        }

        if (request.getTotalDurationSeconds() != null) {
            watchHistory.setTotalDurationSeconds(request.getTotalDurationSeconds());
        }

        watchHistory.setLastWatchedAt(LocalDateTime.now());
    }
}

