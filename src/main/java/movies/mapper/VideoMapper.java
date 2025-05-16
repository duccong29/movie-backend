package movies.mapper;

import movies.dto.response.video.VideoPathResponse;
import movies.dto.response.video.VideoResponse;
import movies.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(source = "movie.id", target = "movieId")
    VideoResponse toVideoResponse(Video video);

    VideoPathResponse toDto(Video video);
//    List<VideoPathResponse> toDtoList(List<Video> videos);
}
