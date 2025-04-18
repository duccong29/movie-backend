package movies.service;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.VideoRequest;
import movies.dto.response.VideoResponse;
import movies.entity.Episode;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

//@Service
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class VideoService {
//    @NonFinal
//    @Value("${spring.files.video}")
//    protected String videoDir;
//
//    @NonFinal
//    @Value("${spring.file.video.hsl}")
//    protected String hlsDir;
//
//    VideoRepository videoRepository;
//    MovieRepository movieRepository;
//    VideoMapper videoMapper;
//
//    @PostConstruct
//    public void init() {
//        // Tạo thư mục HLS nếu chưa tồn tại
//        try {
//            Files.createDirectories(Paths.get(hlsDir));
//        } catch (IOException e) {
//            throw new RuntimeException("Không tạo được thư mục HLS: " + e.getMessage());
//        }
//
//        // Tạo thư mục chứa video gốc nếu chưa tồn tại
//        File fileDir = new File(videoDir);
//        if (!fileDir.exists()) {
//            fileDir.mkdir();
//            log.info("Đã tạo thư mục video: {}", videoDir);
//        } else {
//            log.info("Thư mục video đã tồn tại: {}", videoDir);
//        }
//    }
//
//    public VideoResponse save(VideoRequest request, MultipartFile file) {
//        try {
//            // Lấy thông tin file upload
//            String originalFilename = file.getOriginalFilename();
//            String contentType = file.getContentType();
//            InputStream inputStream = file.getInputStream();
//
//            // Làm sạch tên file và xây dựng đường dẫn lưu trên server
//            String cleanFileName = StringUtils.cleanPath(originalFilename);
//            Path targetPath = Paths.get(videoDir, cleanFileName);
//
//            log.info("Lưu file tại: {}", targetPath);
//            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
//
//            // Khởi tạo Video entity
//            Video video = Video.builder()
//                    .filePath(targetPath.toString())
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            // Nếu video liên kết với Movie, ta tìm movie theo movieId và set cho video
//            if (request.getMovieId() != null) {
//                Movie movie = movieRepository.findById(request.getMovieId())
//                        .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
//                video.setMovie(movie);
//            }
//
//            // Lưu metadata video vào database
//            Video savedVideo = videoRepository.save(video);
//
//            // Xử lý chuyển đổi video sang HLS (sử dụng ffmpeg)
//            processVideo(savedVideo.getId());
//
//            return videoMapper.toVideoResponse(savedVideo);
//        } catch (IOException e) {
//            log.error("Lỗi khi xử lý video: {}", e.getMessage());
//            throw new AppException(ErrorCodes.VIDEO_PROCESSING_ERROR);
//        }
//    }
//
//    public VideoResponse getVideo(String videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
//        return videoMapper.toVideoResponse(video);
//    }
//
//    public String processVideo(String videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
//        Path sourcePath = Paths.get(video.getFilePath());
//        Path outputPath = Paths.get(hlsDir, videoId);
//
//        try {
//            Files.createDirectories(outputPath);
//        } catch (IOException e) {
//            throw new RuntimeException("Không tạo được thư mục HLS cho video: " + videoId);
//        }
//
//        // Lệnh ffmpeg chuyển video sang HLS
//        String ffmpegCmd = String.format(
//                "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\" \"%s/master.m3u8\"",
//                sourcePath, outputPath, outputPath
//        );
//
//        log.info("Chạy lệnh xử lý video: {}", ffmpegCmd);
//
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
//            processBuilder.inheritIO();
//            Process process = processBuilder.start();
//            int exitCode = process.waitFor();
//
//            if (exitCode != 0) {
//                throw new RuntimeException("Xử lý video thất bại, exit code: " + exitCode);
//            }
//
//            // Cập nhật lại hlsPath cho video sau khi xử lý thành công
//            video.setHlsPath(Paths.get(outputPath.toString(), "master.m3u8").toString());
//            videoRepository.save(video);
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Lỗi khi xử lý video: " + e.getMessage());
//        }
//        return videoId;
//    }
//}

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
                    .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
            video.setMovie(movie);
        } else if (request.getEpisodeId() != null) {
            Episode episode = episodeRepository.findById(request.getEpisodeId())
                    .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
            video.setEpisode(episode);
        } else {
            throw new AppException(ErrorCodes.VIDEO_INVALID_OWNER);
        }
    }

    public VideoResponse getVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));
        return videoMapper.toVideoResponse(video);
    }

    public String processVideo(String videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new AppException(ErrorCodes.VIDEO_NOT_FOUND));

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

        return videoId;
    }
}
