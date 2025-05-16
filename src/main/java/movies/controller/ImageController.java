package movies.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.image.ImageRequest;
import movies.dto.response.image.ImageResponse;
import movies.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    /**
     * Upload an image for a specific entity
     *
     * @param file       the image file to upload
     * @param entityType the type of entity (MOVIE, SERIES, EPISODE, USER)
     * @param entityId   the ID of the entity
     * @param imageType  the type of image (poster, thumbnail, avatar, etc.)
     * @return the created image details
     */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ImageResponse> uploadImage(
//            @RequestPart("file") MultipartFile file,
//            @RequestPart("entityType") String entityType,
//            @RequestPart("entityId") String entityId,
//            @RequestPart("imageType") String imageType) {
//
//        try {
//            log.info("Uploading {} image for {}: ID {}", imageType, entityType, entityId);
//
//            // Create request DTO
//            ImageRequest request = ImageRequest.builder()
//                    .entityType(entityType)
//                    .entityId(entityId)
//                    .imageType(imageType)
//                    .build();
//
//            // Upload image using the service
//            ImageResponse response = imageService.uploadImages(file, request);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            log.error("Failed to upload image", e);
//            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
//        }
//    }

    /**
     * Get all images for an entity
     *
     * @param entityType the type of entity (MOVIE, SERIES, EPISODE, USER)
     * @param entityId   the ID of the entity
     * @return a list of image responses
     */
//    @GetMapping("/entity/{entityType}/{entityId}")
//    public ResponseEntity<List<ImageResponse>> getImagesForEntity(
//            @PathVariable String entityType,
//            @PathVariable String entityId) {
//
//        List<ImageResponse> images = imageService.getImagesForEntity(entityType, entityId);
//        return ResponseEntity.ok(images);
//    }

    /**
     * Get images for an entity of a specific image type
     *
     * @param entityType the type of entity (MOVIE, SERIES, EPISODE, USER)
     * @param entityId   the ID of the entity
     * @param imageType  the type of image (poster, thumbnail, avatar, etc.)
     * @return a list of image responses
     */
//    @GetMapping("/entity/{entityType}/{entityId}/type/{imageType}")
//    public ResponseEntity<List<ImageResponse>> getImagesForEntityAndType(
//            @PathVariable String entityType,
//            @PathVariable String entityId,
//            @PathVariable String imageType) {
//
//        List<ImageResponse> images = imageService.getImagesForEntityAndType(entityType, entityId, imageType);
//        return ResponseEntity.ok(images);
//    }

    /**
     * Get the most recent image for an entity of a specific image type
     *
     * @param entityType the type of entity (MOVIE, SERIES, EPISODE, USER)
     * @param entityId   the ID of the entity
     * @param imageType  the type of image (poster, thumbnail, avatar, etc.)
     * @return the most recent image response, if any
     */
//    @GetMapping("/entity/{entityType}/{entityId}/type/{imageType}/latest")
//    public ResponseEntity<ImageResponse> getLatestImageForEntityAndType(
//            @PathVariable String entityType,
//            @PathVariable String entityId,
//            @PathVariable String imageType) {
//
//        Optional<ImageResponse> imageOptional = imageService.getLatestImageForEntityAndType(entityType, entityId, imageType);
//        return imageOptional.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    /**
     * Get an image by its ID
     *
     * @param id the ID of the image
     * @return the image response, if found
     */
//    @GetMapping("/{id}")
//    public ResponseEntity<ImageResponse> getImageById(@PathVariable String id) {
//        Optional<ImageResponse> imageOptional = imageService.getImageById(id);
//        return imageOptional.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    /**
     * Delete an image by its ID
     *
     * @param id the ID of the image to delete
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable String id) {
        boolean deleted = imageService.deleteImage(id);

        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "Image deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Image not found or could not be deleted");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Delete all images for an entity
     *
     * @param entityType the type of entity (MOVIE, SERIES, EPISODE, USER)
     * @param entityId   the ID of the entity
     * @return a response indicating the number of images deleted
     */
//    @DeleteMapping("/entity/{entityType}/{entityId}")
//    public ResponseEntity<Map<String, Object>> deleteAllImagesForEntity(
//            @PathVariable String entityType,
//            @PathVariable String entityId) {
//
//        int count = imageService.deleteAllImagesForEntity(entityType, entityId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", count + " image(s) deleted successfully");
//        response.put("count", count);
//
//        return ResponseEntity.ok(response);
//    }
}
