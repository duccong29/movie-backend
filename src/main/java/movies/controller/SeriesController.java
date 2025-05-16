package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.series.SeriesRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.PageResponse;
import movies.dto.response.series.SeriesResponse;
import movies.service.SeriesService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/series")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeriesController {
    SeriesService seriesService;

    @PostMapping
    ApiResponse<SeriesResponse> createSeries(@Valid @RequestBody SeriesRequest request) {
        return ApiResponse.<SeriesResponse>builder()
                .data(seriesService.createSeries(request))
                .build();
    }

    @PutMapping("/{seriesId}")
    ApiResponse<SeriesResponse> updateSeries(
            @PathVariable("seriesId") String seriesId,
            @Valid @RequestBody SeriesRequest request) {
        return ApiResponse.<SeriesResponse>builder()
                .data(seriesService.updateSeries(seriesId, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SeriesResponse>> getAllSeries() {
        return ApiResponse.<List<SeriesResponse>>builder()
                .data(seriesService.getAllSeries())
                .build();
    }

    @GetMapping("/{seriesId}")
    ApiResponse<SeriesResponse> getSeriesById(@PathVariable("seriesId") String seriesId) {
        return ApiResponse.<SeriesResponse>builder()
                .data(seriesService.getSeriesById(seriesId))
                .build();
    }

    @DeleteMapping("/{seriesId}")
    ApiResponse<String> deleteSeries(@PathVariable("seriesId") String seriesId) {
        seriesService.deleteSeries(seriesId);
        return ApiResponse.<String>builder()
                .data("Series has been deleted")
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<SeriesResponse>> searchSeries(
            @RequestParam("query") String query,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<SeriesResponse>>builder()
                .data(seriesService.searchSeries(query, pageable))
                .build();
    }

    @GetMapping("/genre/{genreId}")
    public ApiResponse<PageResponse<SeriesResponse>> getSeriesByGenre(
            @PathVariable String genreId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<PageResponse<SeriesResponse>>builder()
                .data(seriesService.getSeriesByGenre(genreId, pageable))
                .build();
    }

    @GetMapping("/top-rated")
    public ApiResponse<List<SeriesResponse>> getTopRatedSeries() {
        return ApiResponse.<List<SeriesResponse>>builder()
                .data(seriesService.getTopRatedSeries())
                .build();
    }

    @GetMapping("/latest")
    public ApiResponse<List<SeriesResponse>> getLatestSeries() {
        return ApiResponse.<List<SeriesResponse>>builder()
                .data(seriesService.getLatestSeries())
                .build();
    }
}
