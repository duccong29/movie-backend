package movies.mapper;

import movies.dto.request.Comment.CommentRequest;
import movies.dto.response.Comment.CommentResponse;
import movies.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toComment(CommentRequest commentRequest);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "seriesId", source = "series.id")
    @Mapping(target = "episodeId", source = "episode.id")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    CommentResponse toCommentResponse(Comment comment);

}
