package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.EpisodeRequest;
import movies.dto.request.VideoRequest;
import movies.dto.response.EpisodeResponse;
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
                .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_FOUND));

        Episode episode = episodeMapper.toEpisode(request);
        episode.setSeason(season);

        Episode savedEpisode = episodeRepository.save(episode);

        uploadEpisodeVideoIfPresent(savedEpisode.getId(), videoFile);

        return episodeMapper.toEpisodeResponse(savedEpisode);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public EpisodeResponse updateEpisode(String episodeId, EpisodeRequest request, MultipartFile videoFile) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_FOUND));

        episodeMapper.updateEpisode(request, episode);

        if (request.getSeasonId() != null) {
            Season season = seasonRepository.findById(request.getSeasonId())
                    .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_FOUND));
            episode.setSeason(season);
        }

        Episode savedEpisode = episodeRepository.save(episode);

        uploadEpisodeVideoIfPresent(savedEpisode.getId(), videoFile);

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
                        .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_FOUND))
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEpisode(String episodeId) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new AppException(ErrorCodes.EPISODE_NOT_FOUND);
        }
        episodeRepository.deleteById(episodeId);
    }

    private void uploadEpisodeVideoIfPresent(String episodeId, MultipartFile videoFile) {
        if (videoFile != null && !videoFile.isEmpty()) {
            VideoRequest videoRequest = new VideoRequest();
            videoRequest.setEpisodeId(episodeId);
            videoService.save(videoRequest, videoFile);
        }
    }
}
