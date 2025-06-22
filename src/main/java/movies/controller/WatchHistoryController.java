package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.watchHistory.WatchHistoryRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.watchHistory.WatchHistoryResponse;
import movies.service.WatchHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/watch-history")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryController {
    WatchHistoryService watchHistoryService;

    /**
     * Update movie watch history
     */
    @PostMapping("/movie")
    ApiResponse<WatchHistoryResponse> updateMovieWatchHistory(@Valid @RequestBody WatchHistoryRequest request) {
        return ApiResponse.<WatchHistoryResponse>builder()
                .data(watchHistoryService.updateMovieWatchHistory(request))
                .message("Movie watch history updated successfully")
                .build();
    }

    /**
     * Update episode watch history
     */
    @PostMapping("/episode")
    ApiResponse<WatchHistoryResponse> updateEpisodeWatchHistory(@Valid @RequestBody WatchHistoryRequest request) {
        return ApiResponse.<WatchHistoryResponse>builder()
                .data(watchHistoryService.updateEpisodeWatchHistory(request))
                .message("Episode watch history updated successfully")
                .build();
    }

    /**
     * Get user's all watch history
     */
    @GetMapping("/my-watch-history")
    ApiResponse<List<WatchHistoryResponse>> getUserWatchHistory() {
        return ApiResponse.<List<WatchHistoryResponse>>builder()
                .data(watchHistoryService.getUserWatchHistory())
                .message("User watch history retrieved successfully")
                .build();
    }

    /**
     * Get movie watch history for a user
     */
    @GetMapping("/movie/{movieId}")
    public ApiResponse<WatchHistoryResponse> getMovieWatchHistory(@PathVariable String movieId) {
        return watchHistoryService.getMovieWatchHistory(movieId)
                .map(history -> ApiResponse.<WatchHistoryResponse>builder()
                        .data(history)
                        .message("Movie watch history retrieved successfully")
                        .build())
                .orElseGet(() -> ApiResponse.<WatchHistoryResponse>builder()
                        .data(null)
                        .message("No watch history found for this movie")
                        .build());
    }

    /**
     * Get episode watch history for a user
     */
    @GetMapping("/episode/{episodeId}")
    public ApiResponse<WatchHistoryResponse> getEpisodeWatchHistory(@PathVariable String episodeId) {
        return watchHistoryService.getEpisodeWatchHistory(episodeId)
                .map(history -> ApiResponse.<WatchHistoryResponse>builder()
                        .data(history)
                        .message("Episode watch history retrieved successfully")
                        .build())
                .orElseGet(() -> ApiResponse.<WatchHistoryResponse>builder()
                        .data(null)
                        .message("No watch history found for this episode")
                        .build());
    }

    /**
     * Get series watch history for a user
     */
    @GetMapping("/series/{seriesId}")
    public ApiResponse<List<WatchHistoryResponse>> getSeriesWatchHistory(@PathVariable String seriesId) {
        return ApiResponse.<List<WatchHistoryResponse>>builder()
                .data(watchHistoryService.getSeriesWatchHistory(seriesId))
                .message("Series watch history retrieved successfully")
                .build();
    }

    /**
     * Count movie watches
     */
    @GetMapping("/count/movie/{movieId}")
    ApiResponse<Long> countMovieWatches(@PathVariable String movieId) {
        log.info("Request to count watches for movie: {}", movieId);

        return ApiResponse.<Long>builder()
                .data(watchHistoryService.countMovieWatches(movieId))
                .message("Movie watch count retrieved successfully")
                .build();
    }

    /**
     * Count episode watches
     */
    @GetMapping("/count/episode/{episodeId}")
    ApiResponse<Long> countEpisodeWatches(@PathVariable String episodeId) {
        log.info("Request to count watches for episode: {}", episodeId);

        return ApiResponse.<Long>builder()
                .data(watchHistoryService.countEpisodeWatches(episodeId))
                .message("Episode watch count retrieved successfully")
                .build();
    }

    /**
     * Count user's total watches
     */
    @GetMapping("/count")
    ApiResponse<Long> countUserWatches() {
        return ApiResponse.<Long>builder()
                .data(watchHistoryService.countUserWatches())
                .message("User watch count retrieved successfully")
                .build();
    }

    /**
     * Delete movie watch history
     */
    @DeleteMapping("/movie/{movieId}")
    ApiResponse<Void> deleteMovieWatchHistory(@PathVariable String movieId) {
        watchHistoryService.deleteMovieWatchHistory(movieId);

        return ApiResponse.<Void>builder()
                .message("Movie watch history deleted successfully")
                .build();
    }

    /**
     * Delete episode watch history
     */
    @DeleteMapping("/episode/{episodeId}")
    ApiResponse<Void> deleteEpisodeWatchHistory(@PathVariable String episodeId) {
        watchHistoryService.deleteEpisodeWatchHistory( episodeId);
        return ApiResponse.<Void>builder()
                .message("Episode watch history deleted successfully")
                .build();
    }

    /**
     * Delete watch history by ID
     */
    @DeleteMapping("/{watchHistoryId}")
    ApiResponse<Void> deleteWatchHistory(@PathVariable String watchHistoryId) {
        log.info("Request to delete watch history with ID: {}", watchHistoryId);

        watchHistoryService.deleteWatchHistory(watchHistoryId);

        return ApiResponse.<Void>builder()
                .message("Watch history deleted successfully")
                .build();
    }

}
