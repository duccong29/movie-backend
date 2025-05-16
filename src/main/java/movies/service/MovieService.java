package movies.service;

import event.dto.ImageUploadEvent;
import event.dto.VideoUploadEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedImages;
import movies.dto.request.image.ImageRequest;
import movies.dto.request.movie.MovieRequest;
import movies.dto.request.video.VideoRequest;
import movies.dto.response.image.ImageResponse;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.PageResponse;
import movies.dto.response.video.VideoResponse;
import movies.entity.Genre;
import movies.entity.Image;
import movies.entity.Movie;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.ImageMapper;
import movies.mapper.MovieMapper;
import movies.repository.ImageRepository;
import movies.repository.MovieRepository;
import movies.repository.VideoRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse createMovie(MovieRequest request, MultipartFile videoFile, List<MultipartFile> files) throws IOException {
        log.info("Creating movie with request: {}", request);

        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCodes.MOVIE_EXISTED);
        }

        Movie movie = movieMapper.toMovie(request);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
            movie.setGenres(genres);
        }

        Movie savedMovie = movieRepository.save(movie);

        if (files != null && !files.isEmpty()) {
            imageService.uploadImages(files, movie.getId());
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            videoService.uploadVideo(videoFile, savedMovie.getId());
        }

        MovieResponse response = movieMapper.toMovieResponse(savedMovie);

        log.info("Created movie successfully: {}", response);

        return response;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse updateMovie(String movieId, MovieRequest request, MultipartFile videoFile, MultipartFile imageFile) throws IOException {
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

//        if (imageFile != null && !imageFile.isEmpty()) {
//            ImageRequest imageUploadRequest = ImageRequest.builder()
//                    .entityType(PredefinedImages.MOVIE_ENTITY_TYPE)
//                    .entityId(String.valueOf(movie.getId()))
//                    .imageType(PredefinedImages.IMAGE_TYPE_POSTER)
//                    .build();
//
////            imageService.uploadImage(imageFile, imageUploadRequest);
//        }
        uploadMovieVideoIfPresent(savedMovie.getId(), videoFile);

        return movieMapper.toMovieResponse(movieRepository.save(movie));
    }


    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovieById(String movieId) {
        return movieMapper.toMovieResponse(movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
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
    public List<MovieResponse> getTopRatedMovies() {
        return movieRepository.findTop10ByOrderByAverageRatingDesc()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getLatestMovies() {
        return movieRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
    }

    private void uploadMovieVideoIfPresent(String movieId, MultipartFile videoFile) {
        if (videoFile != null && !videoFile.isEmpty()) {
            VideoRequest videoRequest = new VideoRequest();
            videoRequest.setMovieId(movieId);
            videoService.save(videoRequest, videoFile);
        }
    }

}
