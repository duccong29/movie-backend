package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.series.SeriesRequest;
import movies.dto.response.PageResponse;
import movies.dto.response.series.SeriesResponse;
import movies.entity.Genre;
import movies.entity.Series;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.SeriesMapper;
import movies.repository.SeriesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


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
        if (seriesRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCodes.SERIES_EXISTED);
        }

        Series series = seriesMapper.toSeries(request);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
            series.setGenres(genres);
        }

        Series savesSeries = seriesRepository.save(series);

        return seriesMapper.toSeriesResponse(savesSeries);
    }

    @Transactional
    public SeriesResponse updateSeries(String seriesId, SeriesRequest request) {
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED));

        if (!series.getTitle().equalsIgnoreCase(request.getTitle())
                && seriesRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCodes.SERIES_EXISTED);
        }

        seriesMapper.updateSeries(request, series);

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            Set<Genre> genres = genreService.validateAndGetGenres(request.getGenreIds());
            series.setGenres(genres);
        }



        Series savesSeries = seriesRepository.save(series);

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
            throw new AppException(ErrorCodes.SERIES_NOT_EXISTED);
        }
        seriesRepository.deleteById(seriesId);
    }

    @Transactional(readOnly = true)
    public PageResponse<SeriesResponse> searchSeries(String query, Pageable pageable) {
        Page<Series> seriesPage = seriesRepository.search(query, pageable);

        if (seriesPage == null || seriesPage.isEmpty()) {
            throw new AppException(ErrorCodes.SERIES_NOT_FOUND_BY_QUERY);
        }
        return PageResponse.<SeriesResponse>builder()
                .currentPage(seriesPage.getNumber())
                .totalPages(seriesPage.getTotalPages())
                .pageSize(seriesPage.getSize())
                .totalElements(seriesPage.getTotalElements())
                .data(seriesPage.getContent().stream()
                        .map(seriesMapper::toSeriesResponse)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<SeriesResponse> getSeriesByGenre(String genreId, Pageable pageable) {
        Page<Series> seriesPage = seriesRepository.findByGenreId(genreId, pageable);

        if (seriesPage.isEmpty()) {
            throw new AppException(ErrorCodes.SERIES_NOT_FOUND_BY_GENRE);
        }

        return PageResponse.<SeriesResponse>builder()
                .currentPage(seriesPage.getNumber())
                .totalPages(seriesPage.getTotalPages())
                .pageSize(seriesPage.getSize())
                .totalElements(seriesPage.getTotalElements())
                .data(seriesPage.getContent().stream()
                        .map(seriesMapper::toSeriesResponse)
                        .toList())
                .build();
    }


    @Transactional(readOnly = true)
    public List<SeriesResponse> getTopRatedSeries() {
        return seriesRepository.findTop10ByOrderByAverageRatingDesc()
                .stream()
                .map(seriesMapper::toSeriesResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeriesResponse> getLatestSeries() {
        return seriesRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(seriesMapper::toSeriesResponse)
                .toList();
    }

//    public Set<Series> validateAndGetSeries(Set<String> seriesIds) {
//        if (seriesIds == null || seriesIds.isEmpty()) {
//            throw new AppException(ErrorCodes.SERIES_REQUIRED);
//        }
//        Set<Series> series = new HashSet<>(seriesRepository.findAllById(seriesIds));
//        if (series.size() != seriesIds.size()) {
//            throw new AppException(ErrorCodes.SERIES_NOT_EXISTED);
//        }
//        return series;
//    }
}
