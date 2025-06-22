package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.response.PageResponse;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.series.SeriesResponse;
import movies.entity.Movie;
import movies.entity.Series;
import movies.entity.User;
import movies.entity.WishlistItem;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.MovieMapper;
import movies.mapper.SeriesMapper;
import movies.repository.MovieRepository;
import movies.repository.SeriesRepository;
import movies.repository.WishlistItemRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WishlistService {
    WishlistItemRepository wishlistItemRepository;
    MovieRepository movieRepository;
    SeriesRepository seriesRepository;
    MovieMapper movieMapper;
    SeriesMapper seriesMapper;
    AuthenticationService authenticationService;
    UserService userService;

    @Transactional
    public boolean addMovieToWishlist(String movieId) {
        // 1. Lấy userId từ token và fetch User entity
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        // 2. Nếu đã tồn tại trong wishlist thì trả về false
        if (wishlistItemRepository.existsByUserIdAndMovieId(currentUserId, movieId)) {
            return false;
        }

        // 3. Fetch Movie, nếu không tồn tại thì ném AppException
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        // 4. Tạo và save WishlistItem (chỉ gán movie, series = null)
        WishlistItem wishlistItem = WishlistItem.builder()
                .user(currentUser)
                .movie(movie)
                .build();
        wishlistItemRepository.save(wishlistItem);

        return true;
    }

    @Transactional
    public boolean addSeriesToWishlist(String seriesId) {
        // 1. Lấy userId từ token và fetch User entity
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        // 2. Nếu đã tồn tại trong wishlist thì trả về false
        if (wishlistItemRepository.existsByUserIdAndSeriesId(currentUserId, seriesId)) {
            return false;
        }

        // 3. Fetch Series, nếu không tồn tại thì ném AppException
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED));

        // 4. Tạo và save WishlistItem (chỉ gán series, movie = null)
        WishlistItem wishlistItem = WishlistItem.builder()
                .user(currentUser)
                .series(series)
                .build();
        wishlistItemRepository.save(wishlistItem);

        return true;
    }

    @Transactional
    public boolean removeMovieFromWishlist(String movieId) {
        // 1. Lấy userId từ token
        String currentUserId = authenticationService.getCurrentUserId();

        // 2. Nếu không tồn tại trong wishlist thì trả về false
        if (!wishlistItemRepository.existsByUserIdAndMovieId(currentUserId, movieId)) {
            return false;
        }

        // 3. Xóa
        wishlistItemRepository.deleteByUserIdAndMovieId(currentUserId, movieId);
        return true;
    }

    @Transactional
    public boolean removeSeriesFromWishlist(String seriesId) {
        // 1. Lấy userId từ token
        String currentUserId = authenticationService.getCurrentUserId();

        // 2. Nếu không tồn tại trong wishlist thì trả về false
        if (!wishlistItemRepository.existsByUserIdAndSeriesId(currentUserId, seriesId)) {
            return false;
        }

        // 3. Xóa
        wishlistItemRepository.deleteByUserIdAndSeriesId(currentUserId, seriesId);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isMovieInWishlist(String movieId) {
        // Lấy userId từ token rồi kiểm tra
        String currentUserId = authenticationService.getCurrentUserId();
        return wishlistItemRepository.existsByUserIdAndMovieId(currentUserId, movieId);
    }

    @Transactional(readOnly = true)
    public boolean isSeriesInWishlist(String seriesId) {
        // Lấy userId từ token rồi kiểm tra
        String currentUserId = authenticationService.getCurrentUserId();
        return wishlistItemRepository.existsByUserIdAndSeriesId(currentUserId, seriesId);
    }

    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> getWishlistedMovies(int page, int size) {
        // Lấy userId từ token và kiểm tra User tồn tại
        String currentUserId = authenticationService.getCurrentUserId();
        userService.getUserEntityById(currentUserId);

        // Tạo Pageable với sort theo createdAt giảm dần
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // Lấy page WishlistItem của user
        Page<WishlistItem> wishlistPage =
                wishlistItemRepository.findByUserIdOrderByCreatedAtDesc(currentUserId, pageable);

        // Chuyển nội dung thành MovieResponse
        List<MovieResponse> movieList = wishlistPage.getContent().stream()
                .filter(item -> item.getMovie() != null)
                .map(item -> movieMapper.toMovieResponse(item.getMovie()))
                .toList();

        // Trả về PageResponse
        return PageResponse.<MovieResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(wishlistPage.getTotalElements())
                .totalPages(wishlistPage.getTotalPages())
                .data(movieList)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<SeriesResponse> getWishlistedSeries(int page, int size) {
        // Lấy userId từ token và kiểm tra User tồn tại
        String currentUserId = authenticationService.getCurrentUserId();
        userService.getUserEntityById(currentUserId);

        // Tạo Pageable với sort theo createdAt giảm dần
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // Lấy page WishlistItem của user
        Page<WishlistItem> wishlistPage =
                wishlistItemRepository.findByUserIdOrderByCreatedAtDesc(currentUserId, pageable);

        // Chuyển nội dung thành SeriesResponse
        List<SeriesResponse> seriesList = wishlistPage.getContent().stream()
                .filter(item -> item.getSeries() != null)
                .map(item -> seriesMapper.toSeriesResponse(item.getSeries()))
                .toList();

        // Trả về PageResponse
        return PageResponse.<SeriesResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(wishlistPage.getTotalElements())
                .totalPages(wishlistPage.getTotalPages())
                .data(seriesList)
                .build();
    }

    @Transactional(readOnly = true)
    public Long countWishlistsForMovie(String movieId) {
        return wishlistItemRepository.countWishlistsForMovie(movieId);
    }

    @Transactional(readOnly = true)
    public Long countWishlistsForSeries(String seriesId) {
        return wishlistItemRepository.countWishlistsForSeries(seriesId);
    }
}

