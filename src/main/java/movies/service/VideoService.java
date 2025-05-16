package movies.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import event.dto.VideoUploadEvent;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.config.StorageProperties;
import movies.constant.PredefinedVideos;
import movies.dto.request.video.VideoRequest;
import movies.dto.response.video.VideoResponse;
import movies.entity.Episode;
import movies.entity.Image;
import movies.entity.Movie;
import movies.entity.Video;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.VideoMapper;
import movies.repository.EpisodeRepository;
import movies.repository.MovieRepository;
import movies.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoService {

    @NonFinal
    @Value("${spring.files.video}")
    String videoDir;

    @NonFinal
    @Value("${spring.file.video.hsl}")
    String hlsDir;

    VideoRepository videoRepository;
    MovieRepository movieRepository;
    EpisodeRepository episodeRepository;
    Cloudinary cloudinary;
    StorageProperties storageProperties;
    VideoMapper videoMapper;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(hlsDir));
        } catch (IOException e) {
            throw new RuntimeException("Không tạo được thư mục HLS: " + e.getMessage());
        }

        File fileDir = new File(videoDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
            log.info("Đã tạo thư mục video: {}", videoDir);
        } else {
            log.info("Thư mục video đã tồn tại: {}", videoDir);
        }
    }

    @Transactional
    public VideoResponse uploadVideo(MultipartFile file, String movieId) throws IOException {
        validateVideo(file);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;

        Video video = Video.builder()
                .fileName(uniqueFilename)
                .originalFileName(originalFilename)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .movie(movie) // Set quan hệ trực tiếp
                .isStoredLocally(false)
                .isStoredInCloudinary(false)
                .build();

        try {
            uploadToCloudinary(file, video);
        } catch (Exception e) {
            log.error("Failed to upload video to Cloudinary, falling back to local storage", e);
            uploadLocally(file, video);
        }

        if (!video.getIsStoredLocally() && !video.getIsStoredInCloudinary()) {
            throw new AppException(ErrorCodes.VIDEO_PROCESSING_ERROR);
        }

        Video savedVideo = videoRepository.save(video);
        movie.setVideo(savedVideo);

        movieRepository.save(movie);

        return videoMapper.toVideoResponse(savedVideo);
    }

    public void validateVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCodes.INVALID_VIDEO_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !PredefinedVideos.ALLOWED_FILE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(ErrorCodes.INVALID_VIDEO_FORMAT);
        }

        if (file.getSize() > PredefinedVideos.MAX_FILE_SIZE) {
            throw new AppException(ErrorCodes.INVALID_VIDEO_SIZE);
        }
    }

    @SuppressWarnings("unchecked")
    private void uploadToCloudinary(MultipartFile file, Video video) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "public_id", "videos/" + video.getFileName().substring(0, video.getFileName().lastIndexOf(".")),
                "overwrite", true,
                "resource_type", "video"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        video.setCloudinaryPublicId((String) uploadResult.get("public_id"));
        video.setCloudinaryUrl((String) uploadResult.get("secure_url"));
        video.setIsStoredInCloudinary(true);
        video.setFilePath((String) uploadResult.get("secure_url"));

        log.info("Video uploaded to Cloudinary: {}", video.getCloudinaryUrl());
    }

    private void uploadLocally(MultipartFile file, Video video) throws IOException {
        Path uploadPath = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Failed to create directory for video storage: {}", e.getMessage());
                throw new AppException(ErrorCodes.VIDEO_PROCESSING_FAILED);
            }
        }

        Path targetLocation = uploadPath.resolve(video.getFileName());
        Files.copy(file.getInputStream(), targetLocation);

        video.setIsStoredLocally(true);
        video.setFilePath(targetLocation.toString());
        video.setLocalUrl("/api/videos/local/" + video.getFileName());
    }

    public VideoResponse save(VideoRequest request, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String cleanFileName = StringUtils.cleanPath(originalFilename);
            Path targetPath = Paths.get(videoDir, cleanFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Đã lưu file video: {}", targetPath);

            Video video = Video.builder()
                    .filePath(targetPath.toString())
                    .createdAt(LocalDateTime.now())
                    .build();

            attachToOwner(request, video);

            Video savedVideo = videoRepository.save(video);

            processVideo(savedVideo.getId());

            return videoMapper.toVideoResponse(savedVideo);
        } catch (IOException e) {
            log.error("Lỗi khi lưu video: {}", e.getMessage());
            throw new AppException(ErrorCodes.VIDEO_PROCESSING_ERROR);
        }
    }

    private void attachToOwner(VideoRequest request, Video video) {
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));
            video.setMovie(movie);
        } else if (request.getEpisodeId() != null) {
            Episode episode = episodeRepository.findById(request.getEpisodeId())
                    .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED));
            video.setEpisode(episode);
        } else {
            throw new AppException(ErrorCodes.VIDEO_INVALID_OWNER);
        }
    }

    public VideoResponse getVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_EXISTED));
        return videoMapper.toVideoResponse(video);
    }

    public void processVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_EXISTED));

        Path sourcePath = Paths.get(video.getFilePath());
        Path outputPath = Paths.get(hlsDir, videoId);

        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Không tạo được thư mục HLS: " + videoId);
        }

        String ffmpegCmd = String.format(
                "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\" \"%s/master.m3u8\"",
                sourcePath, outputPath, outputPath
        );

        log.info("Chạy lệnh xử lý video: {}", ffmpegCmd);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Xử lý video thất bại, exit code: " + exitCode);
            }

            video.setHlsPath(outputPath.resolve("master.m3u8").toString());
            videoRepository.save(video);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Lỗi khi xử lý video: " + e.getMessage());
        }

    }
}
