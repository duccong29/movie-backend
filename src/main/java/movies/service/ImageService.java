package movies.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import event.dto.ImageUploadEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.config.CloudinaryConfig;
import movies.config.StorageProperties;
import movies.constant.PredefinedImages;
import movies.dto.request.image.ImageRequest;
import movies.dto.response.image.ImageResponse;
import movies.entity.*;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.ImageMapper;
import movies.repository.ImageRepository;
import movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    ImageRepository imageRepository;
    Cloudinary cloudinary;
    ImageMapper imageMapper;
    StorageProperties storageProperties;
    MovieRepository movieRepository;



    @Transactional
    public List<ImageResponse> uploadImages(List<MultipartFile> files, String movieId) throws IOException {
        validateImage(files);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        List<Image> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID() + extension;
            String detectedType = detectImageType(originalFilename);
            Image image = Image.builder()
                    .fileName(uniqueFilename)
                    .originalFileName(originalFilename)
                    .fileType(file.getContentType())
                    .imageType(detectedType)
                    .fileSize(file.getSize())
                    .movie(movie)
                    .isStoredLocally(false)
                    .isStoredInCloudinary(false)
                    .build();

            try {
                uploadToCloudinary(file, image);
            } catch (Exception e) {
                log.error("Failed to upload image to Cloudinary, falling back to local storage", e);
                uploadLocally(file, image);
            }

            if (!image.getIsStoredLocally() && !image.getIsStoredInCloudinary()) {
                throw new AppException(ErrorCodes.IMAGE_PROCESSING_FAILED);
            }

            Image savedImage = imageRepository.save(image);
            savedImages.add(savedImage);
        }

        movie.getImages().addAll(savedImages);
        movieRepository.save(movie);

        return savedImages.stream()
                .map(imageMapper::toImageResponse)
                .collect(Collectors.toList());
    }


    public ImageResponse getImageById(String id) {
        return imageRepository.findById(id)
                .map(imageMapper::toImageResponse)
                .orElseThrow(() -> new AppException(ErrorCodes.IMAGE_NOT_EXISTED));
    }

    public List<ImageResponse> getImagesByMovie(String movieId) {
        List<Image> images = imageRepository.findByMovieId(movieId);
        return imageRepository.findByMovieId(movieId)
                .stream()
                .map(imageMapper::toImageResponse)
                .toList();

    }


    @Transactional
    public boolean deleteImage(String imageId) {
        Optional<Image> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();

            if (image.getIsStoredInCloudinary() && image.getCloudinaryPublicId() != null) {
                try {
                    cloudinary.uploader().destroy(image.getCloudinaryPublicId(), ObjectUtils.emptyMap());
                } catch (Exception e) {
                    log.error("Failed to delete image from Cloudinary: {}", image.getCloudinaryPublicId(), e);
                }
            }

            // Delete from local storage if stored there
            if (image.getIsStoredLocally() && image.getFilePath() != null) {
                try {
                    Path filePath = Paths.get(image.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    log.error("Failed to delete local image file: {}", image.getFilePath(), e);
                }
            }


            // Delete from database
            imageRepository.delete(image);
            return true;
        }
        return false;
    }

    @Transactional
    public ImageResponse updateImage(String id, ImageResponse updatedImage) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.IMAGE_NOT_EXISTED));

        // Update image fields
        // Note: We only update fields that are meant to be updatable, not storage-related fields
        if (updatedImage.getImageType() != null) {
            image.setImageType(updatedImage.getImageType());
        }

        Image savedImage = imageRepository.save(image);
        return imageMapper.toImageResponse(savedImage);
    }


    public void validateImage(List<MultipartFile> files) {
        files.forEach(file -> {
            if (file == null || file.isEmpty()) {
                throw new AppException(ErrorCodes.INVALID_IMAGE_FILE);
            }
            String contentType = file.getContentType();
            if (contentType == null || !PredefinedImages.ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
                throw new AppException(ErrorCodes.INVALID_IMAGE_FORMAT);
            }

            if (file.getSize() > PredefinedImages.MAX_FILE_SIZE) {
                throw new AppException(ErrorCodes.INVALID_IMAGE_SIZE);
            }
        });
    }

    /**
     * Upload an image to Cloudinary
     */
    @SuppressWarnings("unchecked")
    private void uploadToCloudinary(MultipartFile file, Image image) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", "images/" + image.getFileName().substring(0, image.getFileName().lastIndexOf(".")),
                "overwrite", true,
                "resource_type", "image"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        image.setCloudinaryPublicId((String) uploadResult.get("public_id"));
        image.setCloudinaryUrl((String) uploadResult.get("secure_url"));
        image.setIsStoredInCloudinary(true);
        image.setFilePath((String) uploadResult.get("secure_url"));

        log.info("Image uploaded to Cloudinary: {}", image.getCloudinaryUrl());
    }

    /**
     * Upload an image to local storage
     */
    private void uploadLocally(MultipartFile file, Image image) throws IOException {
        Path uploadPath = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.info("Failed to create directory for image storage: {}", e.getMessage());
                throw new AppException(ErrorCodes.IMAGE_PROCESSING_FAILED);
            }
        }

        Path targetLocation = uploadPath.resolve(image.getFileName());
        Files.copy(file.getInputStream(), targetLocation);

        image.setIsStoredLocally(true);
        image.setFilePath(targetLocation.toString());
        image.setLocalUrl("/api/images/local/" + image.getFileName());

        log.info("Image saved locally: {}", image.getFilePath());
    }

    private String detectImageType(String filename) {
        if (filename == null) {
            return PredefinedImages.OTHER;
        }

        filename = filename.toLowerCase();

        if (filename.contains("poster")) {
            return PredefinedImages.POSTER;
        } else if (filename.contains("backdrop") || filename.contains("background")) {
            return PredefinedImages.BACKDROP;
        } else if (filename.contains("thumbnail") || filename.contains("thumb")) {
            return PredefinedImages.THUMBNAIL;
        } else if (filename.contains("logo")) {
            return PredefinedImages.LOGO;
        } else if (filename.contains("profile")) {
            return PredefinedImages.PROFILE;
        } else if (filename.contains("avatar")) {
            return PredefinedImages.AVATAR;
        } else if (filename.contains("still")) {
            return PredefinedImages.STILL;
        } else if (filename.contains("episode")) {
            return PredefinedImages.EPISODE;
        } else {
            return PredefinedImages.OTHER;
        }
    }
}
