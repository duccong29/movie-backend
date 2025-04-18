package movies.mapper;

import movies.dto.request.SeriesRequest;
import movies.dto.response.SeriesResponse;
import movies.entity.Series;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class, SeasonMapper.class, VideoMapper.class, EpisodeMapper.class})
public interface SeriesMapper {
    Series toSeries(SeriesRequest request);

    SeriesResponse toSeriesResponse(Series series);

    void updateSeries(SeriesRequest request, @MappingTarget Series series);
}
