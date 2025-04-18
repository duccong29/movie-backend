package movies.mapper;

import movies.dto.response.VideoPathResponse;
import movies.dto.response.VideoResponse;
import movies.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(source = "movie.id", target = "movieId")
    VideoResponse toVideoResponse(Video video);

    VideoPathResponse toDto(Video video);
//    List<VideoPathResponse> toDtoList(List<Video> videos);
}
