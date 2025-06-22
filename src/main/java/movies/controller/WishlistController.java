package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.response.ApiResponse;
import movies.dto.response.PageResponse;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.series.SeriesResponse;
import movies.service.WishlistService;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wishlists")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistController {
    WishlistService wishlistService;

    /**
     * Add a movie to the current user's wishlist.
     *
     * @param movieId ID của movie
     * @return ApiResponse chứa true nếu thêm thành công, false nếu đã tồn tại
     */
    @PostMapping("/movie/{movieId}")
    public ApiResponse<Boolean> addMovieToWishlist(@PathVariable String movieId) {
        boolean added = wishlistService.addMovieToWishlist(movieId);
        return ApiResponse.<Boolean>builder()
                .data(added)
                .build();
    }

    /**
     * Remove a movie from the current user's wishlist.
     *
     * @param movieId ID của movie
     * @return ApiResponse chứa true nếu xóa thành công, false nếu không tồn tại
     */
    @DeleteMapping("/movie/{movieId}")
    public ApiResponse<Boolean> removeMovieFromWishlist(@PathVariable String movieId) {
        boolean removed = wishlistService.removeMovieFromWishlist(movieId);
        return ApiResponse.<Boolean>builder()
                .data(removed)
                .build();
    }

    /**
     * Check if a movie is in the current user's wishlist.
     *
     * @param movieId ID của movie
     * @return ApiResponse chứa true nếu đã nằm trong wishlist, false nếu chưa
     */
    @GetMapping("/movie/{movieId}/exists")
    public ApiResponse<Boolean> isMovieInWishlist(@PathVariable String movieId) {
        boolean exists = wishlistService.isMovieInWishlist(movieId);
        return ApiResponse.<Boolean>builder()
                .data(exists)
                .build();
    }

    /**
     * Lấy danh sách paginated các movie trong wishlist hiện tại,
     * trả về PageResponse chứa thông tin phân trang và danh sách MovieResponse.
     *
     * @param page Trang hiện tại (bắt đầu từ 1)
     * @param size Số phần tử trên mỗi trang
     * @return ApiResponse chứa PageResponse<MovieResponse>
     */
    @GetMapping("/movies")
    public ApiResponse<PageResponse<MovieResponse>> getWishlistedMovies(
            @RequestParam int page,
            @RequestParam int size) {
        PageResponse<MovieResponse> pageResponse = wishlistService.getWishlistedMovies(page, size);
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .data(pageResponse)
                .build();
    }

    /**
     * Count how many users have wishlisted a given movie.
     *
     * @param movieId ID của movie
     * @return ApiResponse chứa số lượt wishlist
     */
    @GetMapping("/movie/{movieId}/count")
    public ApiResponse<Long> countWishlistsForMovie(@PathVariable String movieId) {
        Long count = wishlistService.countWishlistsForMovie(movieId);
        return ApiResponse.<Long>builder()
                .data(count)
                .build();
    }


    /**
     * Add a series to the current user's wishlist.
     *
     * @param seriesId ID của series
     * @return ApiResponse chứa true nếu thêm thành công, false nếu đã tồn tại
     */
    @PostMapping("/series/{seriesId}")
    public ApiResponse<Boolean> addSeriesToWishlist(@PathVariable String seriesId) {
        boolean added = wishlistService.addSeriesToWishlist(seriesId);
        return ApiResponse.<Boolean>builder()
                .data(added)
                .build();
    }

    /**
     * Remove a series from the current user's wishlist.
     *
     * @param seriesId ID của series
     * @return ApiResponse chứa true nếu xóa thành công, false nếu không tồn tại
     */
    @DeleteMapping("/series/{seriesId}")
    public ApiResponse<Boolean> removeSeriesFromWishlist(@PathVariable String seriesId) {
        boolean removed = wishlistService.removeSeriesFromWishlist(seriesId);
        return ApiResponse.<Boolean>builder()
                .data(removed)
                .build();
    }

    /**
     * Check if a series is in the current user's wishlist.
     *
     * @param seriesId ID của series
     * @return ApiResponse chứa true nếu đã nằm trong wishlist, false nếu chưa
     */
    @GetMapping("/series/{seriesId}/exists")
    public ApiResponse<Boolean> isSeriesInWishlist(@PathVariable String seriesId) {
        boolean exists = wishlistService.isSeriesInWishlist(seriesId);
        return ApiResponse.<Boolean>builder()
                .data(exists)
                .build();
    }

    /**
     * Lấy danh sách paginated các series trong wishlist hiện tại,
     * trả về PageResponse chứa thông tin phân trang và danh sách SeriesResponse.
     *
     * @param page Trang hiện tại (bắt đầu từ 1)
     * @param size Số phần tử trên mỗi trang
     * @return ApiResponse chứa PageResponse<SeriesResponse>
     */
    @GetMapping("/series")
    public ApiResponse<PageResponse<SeriesResponse>> getWishlistedSeries(
            @RequestParam int page,
            @RequestParam int size) {
        PageResponse<SeriesResponse> pageResponse = wishlistService.getWishlistedSeries(page, size);
        return ApiResponse.<PageResponse<SeriesResponse>>builder()
                .data(pageResponse)
                .build();
    }

    /**
     * Count how many users have wishlisted a given series.
     *
     * @param seriesId ID của series
     * @return ApiResponse chứa số lượt wishlist
     */
    @GetMapping("/series/{seriesId}/count")
    public ApiResponse<Long> countWishlistsForSeries(@PathVariable String seriesId) {
        Long count = wishlistService.countWishlistsForSeries(seriesId);
        return ApiResponse.<Long>builder()
                .data(count)
                .build();
    }

}
