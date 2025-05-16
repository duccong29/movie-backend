package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.movie.MovieRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.movie.MovieResponse;
import movies.dto.response.PageResponse;
import movies.service.MovieService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/movie")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<MovieResponse> createMovie(
            @Valid @ModelAttribute MovieRequest request,
            @RequestPart("videoFile") MultipartFile videoFile,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFile) throws IOException {

        MovieResponse movieResponse = movieService.createMovie(request, videoFile, imageFile);
        return ApiResponse.<MovieResponse>builder()
                .data(movieResponse)
                .build();
    }

    @PutMapping(value = "/{movieId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<MovieResponse> updateMovie(
            @PathVariable("movieId") String movieId,
            @Valid @ModelAttribute MovieRequest request,
            @RequestPart("videoFile") MultipartFile videoFile,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        MovieResponse movieResponse = movieService.updateMovie(movieId, request, videoFile, imageFile);
        return ApiResponse.<MovieResponse>builder()
                .data(movieResponse)
                .build();
    }

    @GetMapping
    ApiResponse<List<MovieResponse>> getAllMovies() {
        return ApiResponse.<List<MovieResponse>>builder()
                .data(movieService.getAllMovies())
                .build();
    }

    @GetMapping("/{movieId}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable("movieId") String movieId) {
        return ApiResponse.<MovieResponse>builder()
                .data(movieService.getMovieById(movieId))
                .build();
    }

    @DeleteMapping("/{movieId}")
    ApiResponse<String> deleteMovie(@PathVariable("movieId") String movieId) {
        movieService.deleteMovie(movieId);
        return ApiResponse.<String>builder()
                .data("Movie has been deleted")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<MovieResponse>> searchMovies(
            @RequestParam("query") String query,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .data(movieService.searchMovies(query, pageable))
                .build();
    }

    @GetMapping("/genre/{genreId}")
    public ApiResponse<PageResponse<MovieResponse>> getMoviesByGenre(
            @PathVariable String genreId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<PageResponse<MovieResponse>>builder()
                .data(movieService.getMoviesByGenre(genreId, pageable))
                .build();
    }

    @GetMapping("/top-rated")
    public ApiResponse<List<MovieResponse>> getTopRatedMovies() {
        return ApiResponse.<List<MovieResponse>>builder()
                .data(movieService.getTopRatedMovies())
                .build();
    }

    @GetMapping("/latest")
    public ApiResponse<List<MovieResponse>> getLatestMovies() {
        return ApiResponse.<List<MovieResponse>>builder()
                .data(movieService.getLatestMovies())
                .build();
    }

    @GetMapping(value = "/videos_hsl/{movieId}/master.m3u8")
    public ResponseEntity<Resource> getHlsMaster(@PathVariable String movieId) {

        Path path = Paths.get("videos_hsl", movieId, "master.m3u8");
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                .body(resource);
    }

    @GetMapping("/videos_hsl/{movieId}/{segment}.ts")
    public ResponseEntity<Resource> serveSegments(
            @PathVariable String movieId,
            @PathVariable String segment) {

        Path path = Paths.get("videos_hsl", movieId, segment + ".ts");
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                .body(resource);
    }
}
