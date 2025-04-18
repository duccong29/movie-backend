package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.MovieRequest;
import movies.dto.request.VideoRequest;
import movies.dto.response.MovieResponse;
import movies.entity.Genre;
import movies.entity.Movie;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.MovieMapper;
import movies.repository.GenreRepository;
import movies.repository.MovieRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    MovieMapper movieMapper;
    VideoService videoService;
    GenreService genreService;
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovieResponse createMovie(MovieRequest request, MultipartFile videoFile) {
        if (movieRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCodes.MOVIE_EXISTED);
        }
        Movie movie = movieMapper.toMovie(request);
        Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
        movie.setGenres(genres);

        Movie savedMovie = movieRepository.save(movie);

//        if (videoFile != null && !videoFile.isEmpty()) {
//            VideoRequest videoRequest = new VideoRequest();
//            videoRequest.setMovieId(savedMovie.getId());
//            videoService.save(videoRequest, videoFile);
//        }
        uploadMovieVideoIfPresent(savedMovie.getId(), videoFile);

        return movieMapper.toMovieResponse(savedMovie);
    }

    public MovieResponse updateMovie(String movieId, MovieRequest request, MultipartFile videoFile) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_FOUND));

        if (!movie.getTitle().equals(request.getTitle()) && movieRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCodes.MOVIE_EXISTED);
        }

        movieMapper.updateMovie(request, movie);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            movie.setGenres(genreService.validateAndGetGenres(request.getGenreIds()));
        }

        Movie savedMovie = movieRepository.save(movie);

//        if (videoFile != null && !videoFile.isEmpty()) {
//            VideoRequest videoRequest = new VideoRequest();
//            videoRequest.setMovieId(savedMovie.getId());
//            videoService.save(videoRequest, videoFile);
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
                .orElseThrow(() -> new AppException(ErrorCodes.GENRE_NOT_EXISTED)));
    }

    //    @Transactional(readOnly = true)
//    public Page<MovieResponse> searchMovies(String title, String genreId, Pageable pageable) {
//        Specification<Movie> spec = Specification.where(null);
//
//        if (StringUtils.hasText(title)) {
//            spec = spec.and((root, query, cb) ->
//                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
//        }
//
//        if (StringUtils.hasText(genreId)) {
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(root.join("genres").get("id"), genreId));
//        }
//
//        return movieRepository.findAll(spec, pageable)
//                .map(movieMapper::toResponse);
//    }
//

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMovie(String id) {
        if (!movieRepository.existsById(id)) {
            throw new AppException(ErrorCodes.MOVIE_NOT_FOUND);
        }
        movieRepository.deleteById(id);
    }

//    private Set<Genre> validateAndGetGenres(Set<String> genreIds) {
//        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));
//
//        if (genres.size() != genreIds.size()) {
//            Set<String> foundIds = genres.stream()
//                    .map(Genre::getId)
//                    .collect(Collectors.toSet());
//
//            Set<String> missingIds = genreIds.stream()
//                    .filter(id -> !foundIds.contains(id))
//                    .collect(Collectors.toSet());
//
//            throw new AppException(ErrorCodes.GENRES_NOT_FOUND);
//        }
//
//        return genres;
//    }
    private void uploadMovieVideoIfPresent(String movieId, MultipartFile videoFile) {
        if (videoFile != null && !videoFile.isEmpty()) {
            VideoRequest videoRequest = new VideoRequest();
            videoRequest.setMovieId(movieId);
            videoService.save(videoRequest, videoFile);
        }
    }

}
