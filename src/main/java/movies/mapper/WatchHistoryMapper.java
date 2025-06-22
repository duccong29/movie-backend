package movies.mapper;

import movies.dto.request.episode.EpisodeRequest;
import movies.dto.request.watchHistory.WatchHistoryRequest;
import movies.dto.response.episode.EpisodeResponse;
import movies.dto.response.watchHistory.WatchHistoryResponse;
import movies.entity.Episode;
import movies.entity.WatchHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EpisodeMapper.class})
public interface WatchHistoryMapper {
    WatchHistory toWatchHistory(WatchHistoryRequest request);

//    @Mapping(target = "movieId", source = "movie.id")
//    @Mapping(target = "seriesId", source = "series.id")
//    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "userId", source = "user.id")
//    @Mapping(source = "episode.season.series.id", target = "seriesId")
    WatchHistoryResponse toWatchHistoryResponse(WatchHistory history);
}
