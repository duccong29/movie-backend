package movies.mapper;

import movies.dto.request.SeasonRequest;
import movies.dto.response.SeasonResponse;
import movies.entity.Season;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EpisodeMapper.class, VideoMapper.class})
public interface SeasonMapper {
    Season toSeason(SeasonRequest request);

//    @Mapping(source = "episodes.id", target = "episodeIds")
    SeasonResponse toSeasonResponse(Season season);

    void updateSeason(SeasonRequest request, @MappingTarget Season season);
}
