package movies.mapper;

import movies.dto.request.season.SeasonCreationRequest;
import movies.dto.request.season.SeasonUpdateRequest;
import movies.dto.response.season.SeasonResponse;
import movies.entity.Season;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EpisodeMapper.class, VideoMapper.class})
public interface SeasonMapper {
    Season toSeason(SeasonCreationRequest request);

//    @Mapping(source = "episodes.id", target = "episodeIds")
    SeasonResponse toSeasonResponse(Season season);

    void updateSeason(SeasonUpdateRequest request, @MappingTarget Season season);
}
