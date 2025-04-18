package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.EpisodeRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.EpisodeResponse;
import movies.service.EpisodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/episode")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeController {
     EpisodeService episodeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EpisodeResponse> createEpisode(
            @ModelAttribute EpisodeRequest request,
            @RequestPart("videoFile") MultipartFile videoFile) {

        EpisodeResponse response = episodeService.createEpisode(request, videoFile);
        return ApiResponse.<EpisodeResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping(value = "/{episodeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<EpisodeResponse> updateEpisode(
            @PathVariable("episodeId") String episodeId,
            @ModelAttribute EpisodeRequest request,
            @RequestPart("videoFile") MultipartFile videoFile) {

        EpisodeResponse response = episodeService.updateEpisode(episodeId, request, videoFile);
        return ApiResponse.<EpisodeResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<EpisodeResponse>> getAllEpisodes() {
        return ApiResponse.<List<EpisodeResponse>>builder()
                .data(episodeService.getAllEpisodes())
                .build();
    }

    @GetMapping("/{episodeId}")
    public ApiResponse<EpisodeResponse> getEpisodeById(@PathVariable("episodeId") String episodeId) {
        return ApiResponse.<EpisodeResponse>builder()
                .data(episodeService.getEpisodeById(episodeId))
                .build();
    }

    @DeleteMapping("/{episodeId}")
    public ApiResponse<String> deleteEpisode(@PathVariable("episodeId") String episodeId) {
        episodeService.deleteEpisode(episodeId);
        return ApiResponse.<String>builder()
                .data("Episode has been deleted")
                .build();
    }
}
