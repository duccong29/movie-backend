package movies.service;

import jakarta.annotation.security.PermitAll;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.SeriesRequest;
import movies.dto.response.SeriesResponse;
import movies.entity.Genre;
import movies.entity.Series;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.SeriesMapper;
import movies.repository.GenreRepository;
import movies.repository.SeriesRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeriesService {
    SeriesRepository seriesRepository;
    SeriesMapper seriesMapper;
    GenreService genreService;


    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SeriesResponse createSeries(SeriesRequest request) {
        validateUniqueTitle(request.getTitle());

        Series series = seriesMapper.toSeries(request);
        series.setAverageRating(0.0);
        Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
        series.setGenres(genres);
//        series.setSeasons(new ArrayList<>());

        Series savesSeries = seriesRepository.save(series);

        return seriesMapper.toSeriesResponse(savesSeries);
    }

    @Transactional
    public SeriesResponse updateSeries(String seriesId, SeriesRequest request) {
        Series existingSeries = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED));

        if (!existingSeries.getTitle().equalsIgnoreCase(request.getTitle())) {
            validateUniqueTitle(request.getTitle());
        }

        Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
        existingSeries.setGenres(genres);

        seriesMapper.updateSeries(request, existingSeries);

        Series savesSeries =seriesRepository.save(existingSeries);

        return seriesMapper.toSeriesResponse(savesSeries);
    }

    @Transactional(readOnly = true)
    public List<SeriesResponse> getAllSeries() {
        return seriesRepository.findAll()
                .stream()
                .map(seriesMapper::toSeriesResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeriesResponse getSeriesById(String seriesId) {
        return seriesMapper.toSeriesResponse(seriesRepository.findById(seriesId)
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSeries(String seriesId) {
        if (!seriesRepository.existsById(seriesId)) {
            throw new AppException(ErrorCodes.SERIES_NOT_FOUND);
        }
        seriesRepository.deleteById(seriesId);
    }

    private void validateUniqueTitle(String title) {
        if (seriesRepository.existsByTitleIgnoreCase(title)) {
            throw new AppException(ErrorCodes.SERIES_ALREADY_EXISTS);
        }
    }
//    private Set<Genre> validateAndGetGenres(Set<String> genreIds) {
//        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));
//
//        if (genres.size() != genreIds.size()) {
//            Set<String> foundIds = genres.stream()
//                    .map(Genre::getId)
//                    .collect(Collectors.toSet());
//
//            Set<String> missingIds = genreIds.stream()
//                    .filter(id -> !foundIds.contains(id))
//                    .collect(Collectors.toSet());
//
//            throw new AppException(ErrorCodes.GENRES_NOT_FOUND);
//        }
//
//        return genres;
//    }
}
