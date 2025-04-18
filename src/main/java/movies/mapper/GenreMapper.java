package movies.mapper;

import movies.dto.request.GenreRequest;
import movies.dto.response.GenreNamesResponse;
import movies.dto.response.GenreResponse;
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
