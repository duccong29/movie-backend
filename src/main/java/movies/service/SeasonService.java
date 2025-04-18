package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.SeasonRequest;
import movies.dto.request.SeriesRequest;
import movies.dto.response.SeasonResponse;
import movies.dto.response.SeriesResponse;
import movies.entity.Genre;
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
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeasonService {
    SeasonRepository seasonRepository;
    SeasonMapper seasonMapper;
    SeriesService seriesService;
    SeriesRepository seriesRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonResponse createSeason(SeasonRequest request) {
        Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_FOUND));

        Season season = seasonMapper.toSeason(request);
        season.setSeries(series);

        Season savedSeason = seasonRepository.save(season);

        return seasonMapper.toSeasonResponse(savedSeason);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeasonResponse updateSeason(String seasonId, SeasonRequest request) {
        Season existingSeason = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new AppException(ErrorCodes.SEASON_ALREADY_EXISTS));

        seasonMapper.updateSeason(request, existingSeason);

        Season updatedSeason = seasonRepository.save(existingSeason);

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
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSeason(String seasonId) {
        if (!seasonRepository.existsById(seasonId)) {
            throw new AppException(ErrorCodes.SEASON_NOT_FOUND);
        }
        seasonRepository.deleteById(seasonId);
    }
}
