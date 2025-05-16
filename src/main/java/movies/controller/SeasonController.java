package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.season.SeasonCreationRequest;
import movies.dto.request.season.SeasonUpdateRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.season.SeasonResponse;
import movies.service.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/season")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeasonController {
    SeasonService seasonService;

    @PostMapping
    ApiResponse<SeasonResponse> createSeason(@Valid @RequestBody SeasonCreationRequest request) {
        return ApiResponse.<SeasonResponse>builder()
                .data(seasonService.createSeason(request))
                .build();
    }

    @PutMapping("/{seasonId}")
    ApiResponse<SeasonResponse> updateSeason(
            @PathVariable("seasonId") String seasonId,
            @Valid @RequestBody SeasonUpdateRequest request) {
        return ApiResponse.<SeasonResponse>builder()
                .data(seasonService.updateSeason(seasonId, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SeasonResponse>> getAllSeason() {
        return ApiResponse.<List<SeasonResponse>>builder()
                .data(seasonService.getAllSeason())
                .build();
    }

    @GetMapping("/{seasonId}")
    ApiResponse<SeasonResponse> getSeasonById(@PathVariable("seasonId") String seasonId) {
        return ApiResponse.<SeasonResponse>builder()
                .data(seasonService.getSeasonById(seasonId))
                .build();
    }

    @DeleteMapping("/{seasonId}")
    ApiResponse<String> deleteSeason(@PathVariable("seasonId") String seasonId) {
        seasonService.deleteSeason(seasonId);
        return ApiResponse.<String>builder()
                .data("Season has been deleted")
                .build();
    }

    @GetMapping("/series/{seriesId}")
    public ApiResponse<List<SeasonResponse>> getSeasonsBySeriesId(@PathVariable String seriesId) {
        return ApiResponse.<List<SeasonResponse>>builder()
                .data(seasonService.getSeasonsBySeriesId(seriesId))
                .build();
    }
}

