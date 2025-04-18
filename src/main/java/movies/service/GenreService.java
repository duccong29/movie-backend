package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.GenreRequest;
import movies.dto.response.GenreResponse;
import movies.entity.Genre;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.GenreMapper;
import movies.repository.GenreRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse createGenre(GenreRequest request) {
        if (genreRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCodes.GENRE_ALREADY_EXISTS);
        }
        Genre genre = genreMapper.toGenre(request);
        Genre savedGenre = genreRepository.save(genre);
        log.info("Created new genre with name: {}", request.getName());
        return genreMapper.toGenreResponse(savedGenre);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse updateGenre(String genreId, GenreRequest request) {
        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new AppException(ErrorCodes.GENRE_NOT_EXISTED));

        if (genreRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCodes.GENRE_ALREADY_EXISTS);
        }

        genreMapper.updateGenre(request, existingGenre);
        Genre savedGenre = genreRepository.save(existingGenre);
        log.info("Updated genre with id: {}", genreId);
        return genreMapper.toGenreResponse(savedGenre);
    }

    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toGenreResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GenreResponse getGenreById(String genreId) {
        return genreMapper.toGenreResponse(genreRepository.findById(genreId)
                .orElseThrow(() -> new AppException(ErrorCodes.GENRE_NOT_EXISTED)));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGenre(String genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw new AppException(ErrorCodes.GENRE_NOT_EXISTED);
        }
        genreRepository.deleteById(genreId);
        log.info("Deleted genre with id: {}", genreId);
    }

    public Set<Genre> validateAndGetGenres(Set<String> genreIds) {
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));

        if (genres.size() != genreIds.size()) {
            Set<String> foundIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<String> missingIds = genreIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new AppException(ErrorCodes.GENRES_NOT_FOUND);
        }

        return genres;
    }
}
