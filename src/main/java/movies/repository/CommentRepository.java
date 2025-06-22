package movies.repository;

import movies.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {
    Page<Comment> findByMovieId(String movieId, Pageable pageable);

    Page<Comment> findBySeriesId(String seriesId, Pageable pageable);

    Page<Comment> findByEpisodeId(String episodeId, Pageable pageable);

    Page<Comment> findByUserId(String userId, Pageable pageable);

    Page<Comment> findByParentCommentId(String parentCommentId, Pageable pageable);

}
