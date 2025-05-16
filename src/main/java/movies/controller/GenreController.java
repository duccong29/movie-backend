package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.genre.GenreRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.genre.GenreResponse;
import movies.service.GenreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/genre")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreService genreService;

    @PostMapping
    ApiResponse<GenreResponse> createGenre(@Valid @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .data(genreService.createGenre(request))
                .build();
    }

    @PutMapping("/{genreId}")
    ApiResponse<GenreResponse> updateGenre(
            @PathVariable("genreId") String genreId,
            @Valid @RequestBody GenreRequest request) {
        return ApiResponse.<GenreResponse>builder()
                .data(genreService.updateGenre(genreId, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<GenreResponse>> getAllGenre() {
        return ApiResponse.<List<GenreResponse>>builder()
                .data(genreService.getAllGenres())
                .build();
    }

    @GetMapping("/{genreId}")
    ApiResponse<GenreResponse> getGenreById(@PathVariable("genreId") String genreId) {
        return ApiResponse.<GenreResponse>builder()
                .data(genreService.getGenreById(genreId))
                .build();
    }

    @DeleteMapping("/{genreId}")
    ApiResponse<String> deleteGenre(@PathVariable("genreId") String genreId) {
        genreService.deleteGenre(genreId);
        return ApiResponse.<String>builder()
                .data("Genre has been deleted")
                .build();
    }
}
