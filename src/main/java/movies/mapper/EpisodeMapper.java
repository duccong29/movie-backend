package movies.mapper;

import movies.dto.request.EpisodeRequest;
import movies.dto.response.EpisodeResponse;
import movies.entity.Episode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {VideoMapper.class})
public interface EpisodeMapper {

    Episode toEpisode(EpisodeRequest request);

    @Mapping(source = "videos", target = "videoPaths")
    @Mapping(source = "season.id", target = "seasonId")
    EpisodeResponse toEpisodeResponse(Episode episode);

    void updateEpisode(EpisodeRequest request, @MappingTarget Episode episode);
}
