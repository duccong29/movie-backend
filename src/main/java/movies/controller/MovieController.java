package movies.controller;

import jakarta.annotation.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.MovieRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.MovieResponse;
import movies.service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            @ModelAttribute MovieRequest request,
            @RequestPart("videoFile") MultipartFile videoFile) {

        MovieResponse movieResponse = movieService.createMovie(request, videoFile);
        return ApiResponse.<MovieResponse>builder()
                .data(movieResponse)
                .build();
    }

    @PutMapping(value = "/{movieId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<MovieResponse> updateMovie(
            @PathVariable("movieId") String movieId,
            @ModelAttribute MovieRequest request,
            @RequestPart("videoFile") MultipartFile videoFile) {
        MovieResponse movieResponse = movieService.updateMovie(movieId, request, videoFile);
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


}
