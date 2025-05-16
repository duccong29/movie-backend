package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.season.SeasonCreationRequest;
import movies.dto.request.season.SeasonUpdateRequest;
import movies.dto.response.season.SeasonResponse;
import movies.entity.Season;
import movies.entity.Series;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.SeasonMapper;
import movies.repository.SeasonRepository;
import movies.repository.SeriesRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeasonService {
    SeasonRepository seasonRepository;
    SeasonMapper seasonMapper;
    SeriesRepository seriesRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonResponse createSeason(SeasonCreationRequest request) {
        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED));

        if (seasonRepository.findBySeriesIdAndSeasonNumber(series.getId(), request.getSeasonNumber()).isPresent()) {
            throw new AppException(ErrorCodes.SEASON_EXISTED);
        }

        Season season = seasonMapper.toSeason(request);
        season.setSeries(series);

        Season savedSeason = seasonRepository.save(season);

        return seasonMapper.toSeasonResponse(savedSeason);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonResponse updateSeason(String seasonId, SeasonUpdateRequest request) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_EXISTED));

        seasonMapper.updateSeason(request, season);

        Season updatedSeason = seasonRepository.save(season);

        return seasonMapper.toSeasonResponse(updatedSeason);

    }

    @Transactional(readOnly = true)
    public List<SeasonResponse> getAllSeason() {
        return seasonRepository.findAll()
                .stream()
                .map(seasonMapper::toSeasonResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeasonResponse getSeasonById(String seasonId) {
        return seasonMapper.toSeasonResponse(seasonRepository.findById(seasonId)
                .orElseThrow(() -> new AppException(ErrorCodes.SEASON_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSeason(String seasonId) {
        if (!seasonRepository.existsById(seasonId)) {
            throw new AppException(ErrorCodes.SEASON_NOT_EXISTED);
        }
        seasonRepository.deleteById(seasonId);
    }

    @Transactional(readOnly = true)
    public List<SeasonResponse> getSeasonsBySeriesId(String seriesId) {
        if (!seriesRepository.existsById(seriesId)) {
            throw new AppException(ErrorCodes.SERIES_NOT_EXISTED);
        }

        List<Season> seasons = seasonRepository.findBySeriesIdOrderBySeasonNumber(seriesId);
        return seasons.stream()
                .map(seasonMapper::toSeasonResponse)
                .collect(Collectors.toList());
    }
}
