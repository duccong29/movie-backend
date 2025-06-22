package movies.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedImages;
import movies.dto.request.movie.MovieRequest;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.PageResponse;
import movies.entity.Genre;
import movies.entity.Movie;
import movies.entity.User;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.MovieMapper;
import movies.repository.MovieRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    MovieRepository movieRepository;
    MovieMapper movieMapper;
    VideoService videoService;
    GenreService genreService;
    ImageService imageService;
    AuthenticationService authenticationService;
    UserService userService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "movies", allEntries = true)
    public MovieResponse createMovie(MovieRequest request, MultipartFile videoFile, List<MultipartFile> files) throws IOException {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCodes.MOVIE_EXISTED);
        }

        Movie movie = movieMapper.toMovie(request);
        movie.setUser(currentUser);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
            movie.setGenres(genres);
        }

        Movie savedMovie = movieRepository.save(movie);

        if (files != null && !files.isEmpty()) {
            imageService.uploadImages(files, movie.getId(), PredefinedImages.MOVIE_ENTITY_TYPE);
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            videoService.uploadVideo(videoFile, savedMovie.getId(), PredefinedImages.MOVIE_ENTITY_TYPE);
        }

        MovieResponse response = movieMapper.toMovieResponse(savedMovie);

        log.info("Created movie successfully: {}", response);

        return response;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "movies", key = "#movieId")
    public MovieResponse updateMovie(String movieId, MovieRequest request, MultipartFile videoFile, List<MultipartFile> files) throws IOException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        if (!movie.getTitle().equalsIgnoreCase(request.getTitle())
                && movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCodes.MOVIE_EXISTED);
        }

        movieMapper.updateMovie(request, movie);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
            movie.setGenres(genres);
        }
        Movie savedMovie = movieRepository.save(movie);

        if (files != null && !files.isEmpty()) {
            imageService.uploadImages(files, movie.getId(), PredefinedImages.MOVIE_ENTITY_TYPE);
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            videoService.uploadVideo(videoFile, savedMovie.getId(), PredefinedImages.MOVIE_ENTITY_TYPE);
        }

        MovieResponse response = movieMapper.toMovieResponse(savedMovie);

        log.info("Update movie successfully: {}", response);

        return response;
    }


    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "movies", key = "#movieId")
    public MovieResponse getMovieById(String movieId) {
        log.info("Fetching movie by ID: {}", movieId);
        return movieMapper.toMovieResponse(movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "movies", key = "#movieId")
    public void deleteMovie(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        movieRepository.delete(movie);
    }

    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> searchMovies(String query, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.search(query, pageable);

        if (moviePage == null || moviePage.isEmpty()) {
            throw new AppException(ErrorCodes.MOVIES_NOT_FOUND_BY_QUERY);
        }
        return PageResponse.<MovieResponse>builder()
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .pageSize(moviePage.getSize())
                .totalElements(moviePage.getTotalElements())
                .data(moviePage.getContent().stream()
                        .map(movieMapper::toMovieResponse)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> getMoviesByGenre(String genreId, Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findByGenreId(genreId, pageable);

        if (moviePage.isEmpty()) {
            throw new AppException(ErrorCodes.MOVIES_NOT_FOUND_BY_GENRE);
        }

        return PageResponse.<MovieResponse>builder()
                .currentPage(moviePage.getNumber())
                .totalPages(moviePage.getTotalPages())
                .pageSize(moviePage.getSize())
                .totalElements(moviePage.getTotalElements())
                .data(moviePage.getContent().stream()
                        .map(movieMapper::toMovieResponse)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "popularContent", key = "'topRatedMovies'")
    public List<MovieResponse> getTopRatedMovies() {
        return movieRepository.findTop10ByOrderByAverageRatingDesc()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "popularContent", key = "'latestMovies'")
    public List<MovieResponse> getLatestMovies() {
        return movieRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }


}
