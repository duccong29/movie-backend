package movies.mapper;

import movies.dto.request.movie.MovieRequest;
import movies.dto.response.movie.MovieResponse;
import movies.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {VideoMapper.class, GenreMapper.class, ImageMapper.class})
public interface MovieMapper {

    Movie toMovie(MovieRequest request);

//    @Mapping(source = "video", target = "videoPaths")
    @Mapping(source = "genres", target = "genres")
    @Mapping(source = "video",  target = "video")
    @Mapping(source = "images", target = "images")
    MovieResponse toMovieResponse(Movie movie);

    void updateMovie(MovieRequest request, @MappingTarget Movie movie);


}
