package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.episode.EpisodeRequest;
import movies.dto.request.video.VideoRequest;
import movies.dto.response.episode.EpisodeResponse;
import movies.entity.Episode;
import movies.entity.Season;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.EpisodeMapper;
import movies.repository.EpisodeRepository;
import movies.repository.SeasonRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeService {
    EpisodeRepository episodeRepository;
    SeasonRepository seasonRepository;
    EpisodeMapper episodeMapper;
    VideoService videoService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EpisodeResponse createEpisode(EpisodeRequest request, MultipartFile videoFile) {
        Season season = seasonRepository.findById(request.getSeasonId())
                .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_EXISTED));

        if (episodeRepository.findBySeasonIdAndEpisodeNumber(season.getId(), request.getEpisodeNumber()).isPresent()) {
            log.warn("Episode already exists with title: {} in season: {}", request.getTitle(), request.getSeasonId());
            throw new AppException(ErrorCodes.EPISODE_EXISTED);
        }
        Episode episode = episodeMapper.toEpisode(request);
        episode.setSeason(season);

        Episode savedEpisode = episodeRepository.save(episode);

        try {
            uploadEpisodeVideoIfPresent(savedEpisode.getId(), videoFile);
        } catch (Exception e) {
            log.error("Video upload failed for episode: {}", savedEpisode.getId(), e);
            throw new AppException(ErrorCodes.VIDEO_PROCESSING_ERROR);
        }

        return episodeMapper.toEpisodeResponse(savedEpisode);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EpisodeResponse updateEpisode(String episodeId, EpisodeRequest request, MultipartFile videoFile) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED));

        if (!episode.getSeason().getId().equals(request.getSeasonId()) ||
                !episode.getEpisodeNumber().equals(request.getEpisodeNumber())) {
            episodeRepository.findBySeasonIdAndEpisodeNumber(request.getSeasonId(), request.getEpisodeNumber())
                    .ifPresent(existingEpisode -> {
                        if (!existingEpisode.getId().equals(episodeId)) {
                            throw new AppException(ErrorCodes.EPISODE_EXISTED);
                        }
                    });

            if (!episode.getSeason().getId().equals(request.getSeasonId())) {
                Season season = seasonRepository.findById(request.getSeasonId())
                        .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_EXISTED));
                episode.setSeason(season);
            }
        }
        episodeMapper.updateEpisode(request, episode);

        Episode savedEpisode = episodeRepository.save(episode);
        try {
            uploadEpisodeVideoIfPresent(savedEpisode.getId(), videoFile);
        } catch (Exception e) {
            log.error("Video upload failed during update for episode: {}", savedEpisode.getId(), e);
            throw new AppException(ErrorCodes.VIDEO_PROCESSING_ERROR);
        }

        return episodeMapper.toEpisodeResponse(savedEpisode);
    }

    @Transactional(readOnly = true)
    public List<EpisodeResponse> getAllEpisodes() {
        return episodeRepository.findAll()
                .stream()
                .map(episodeMapper::toEpisodeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EpisodeResponse getEpisodeById(String episodeId) {
        return episodeMapper.toEpisodeResponse(
                episodeRepository.findById(episodeId)
                        .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED))
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEpisode(String episodeId) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new AppException(ErrorCodes.EPISODE_NOT_EXISTED);
        }
        episodeRepository.deleteById(episodeId);
    }

    @Transactional(readOnly = true)
    public List<EpisodeResponse> getEpisodesBySeasonId(String seasonId) {
        if (!seasonRepository.existsById(seasonId)) {
            throw new AppException(ErrorCodes.SEASON_NOT_EXISTED);
        }

        List<Episode> episodes = episodeRepository.findBySeasonIdOrderByEpisodeNumber(seasonId);
        return episodes.stream()
                .map(episodeMapper::toEpisodeResponse)
                .collect(Collectors.toList());
    }

    private void uploadEpisodeVideoIfPresent(String episodeId, MultipartFile videoFile) {
        if (videoFile != null && !videoFile.isEmpty()) {
            VideoRequest videoRequest = new VideoRequest();
            videoRequest.setEpisodeId(episodeId);
            videoService.save(videoRequest, videoFile);
        }
    }

}
