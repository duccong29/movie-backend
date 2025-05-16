package movies.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.video.VideoRequest;
import movies.dto.response.video.VideoResponse;
import movies.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoController {
    VideoService videoService;

    @PostMapping("/upload/{movieId}")
    public ResponseEntity<VideoResponse> uploadVideo(
            @PathVariable String movieId,
            @RequestParam("file") MultipartFile file) {

        try {
            VideoResponse videoResponse = videoService.uploadVideo(file, movieId);
            return ResponseEntity.ok(videoResponse);
        } catch (Exception e) {
            log.error("Error uploading video for movie {}: {}", movieId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> uploadVideo(
            @PathVariable("movieId") VideoRequest request,
            @RequestPart("file") MultipartFile file) {

        VideoResponse response = videoService.save(request, file);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
