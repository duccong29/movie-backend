package movies.mapper;

import movies.dto.request.MovieRequest;
import movies.dto.response.MovieResponse;
import movies.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {VideoMapper.class, GenreMapper.class})
public interface MovieMapper {

    Movie toMovie(MovieRequest request);

    @Mapping(source = "videos", target = "videoPaths")
    @Mapping(source = "genres", target = "genres")
    MovieResponse toMovieResponse(Movie movie);

    void updateMovie(MovieRequest request, @MappingTarget Movie movie);


}
