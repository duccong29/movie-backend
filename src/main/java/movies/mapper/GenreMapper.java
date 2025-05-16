package movies.mapper;

import movies.dto.request.genre.GenreRequest;
import movies.dto.response.genre.GenreNamesResponse;
import movies.dto.response.genre.GenreResponse;
import movies.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre toGenre (GenreRequest request);
    GenreResponse toGenreResponse(Genre genre);

    GenreNamesResponse toDto(Genre genre);

    void updateGenre (GenreRequest request, @MappingTarget Genre genre);
}
