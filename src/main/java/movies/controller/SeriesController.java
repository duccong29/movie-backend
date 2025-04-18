package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.SeriesRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.SeriesResponse;
import movies.service.SeriesService;
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
    ApiResponse<SeriesResponse> createSeries(@RequestBody SeriesRequest request) {
        return ApiResponse.<SeriesResponse>builder()
                .data(seriesService.createSeries(request))
                .build();
    }

    @PutMapping("/{seriesId}")
    ApiResponse<SeriesResponse> updateSeries(
            @PathVariable("seriesId") String seriesId,
            @RequestBody SeriesRequest request) {
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
}
