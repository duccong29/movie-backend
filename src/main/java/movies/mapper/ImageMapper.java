package movies.mapper;

import movies.dto.request.image.ImageRequest;
import movies.dto.response.image.ImageResponse;
import movies.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "movie", ignore = true)
    Image toImage(ImageResponse response);


    ImageResponse toImageResponse(Image image);


}
