package movies.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.Comment.CommentRequest;
import movies.dto.response.Comment.CommentResponse;
import movies.dto.response.PageResponse;
import movies.entity.*;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import movies.mapper.CommentMapper;
import movies.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    UserService userService;
    AuthenticationService authenticationService;
    MovieRepository movieRepository;
    SeriesRepository seriesRepository;
    EpisodeRepository episodeRepository;
    CommentMapper commentMapper;
    NotificationService notificationService;
    SimpMessagingTemplate messagingTemplate;

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        String currentUserId = authenticationService.getCurrentUserId();
        User currentUser = userService.getUserEntityById(currentUserId);

        Comment comment = commentMapper.toComment(commentRequest);
        comment.setUser(currentUser);

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = findParentComment(commentRequest.getParentCommentId());
            comment.setParentComment(parentComment);
        }

        associateContent(comment, commentRequest);

        Comment savedComment = commentRepository.save(comment);

        broadcastComment(savedComment);

        processNotifications(savedComment);

        return commentMapper.toCommentResponse(savedComment);
    }


    @Transactional(readOnly = true)
    public CommentResponse getCommentById(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.COMMENT_NOT_EXISTED));
        return commentMapper.toCommentResponse(comment);
    }


    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsByMovieId(String movieId, int page, int size) {
        if (!movieRepository.existsById(movieId)) {
            throw new AppException(ErrorCodes.MOVIE_NOT_EXISTED);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Comment> commentPage = commentRepository.findByMovieId(movieId, pageable);

        List<CommentResponse> commentResponses = commentPage.getContent()
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .data(commentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsBySeriesId(String seriesId, int page, int size) {
        if (!seriesRepository.existsById(seriesId)) {
            throw new AppException(ErrorCodes.SERIES_NOT_EXISTED);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Comment> commentPage = commentRepository.findBySeriesId(seriesId, pageable);

        List<CommentResponse> commentResponses = commentPage
                .map(commentMapper::toCommentResponse)
                .getContent();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .data(commentResponses)
                .build();
    }


    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsByEpisodeId(String episodeId, int page, int size) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new AppException(ErrorCodes.EPISODE_NOT_EXISTED);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Comment> commentPage = commentRepository.findByEpisodeId(episodeId, pageable);

        List<CommentResponse> commentResponses = commentPage.getContent()
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .data(commentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getMyComments(int page, int size) {
        String userId = authenticationService.getCurrentUserId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Comment> commentPage = commentRepository.findByUserId(userId, pageable);

        List<CommentResponse> commentResponses = commentPage.getContent()
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .data(commentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getAllComments(int page, int size) {
        if (!authenticationService.isAdmin()) {
            throw new AppException(ErrorCodes.UNAUTHORIZED);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Comment> commentPage = commentRepository.findAll(pageable);

        List<CommentResponse> commentResponses = commentPage.getContent()
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .data(commentResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentReplies(String parentId, int page, int size) {
        if (!commentRepository.existsById(parentId)) {
            throw new AppException(ErrorCodes.COMMENT_NOT_EXISTED);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt"));
        Page<Comment> repliesPage = commentRepository.findByParentCommentId(parentId, pageable);

        List<CommentResponse> replies = repliesPage.getContent()
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(repliesPage.getTotalElements())
                .totalPages(repliesPage.getTotalPages())
                .data(replies)
                .build();
    }


    @Transactional
    public CommentResponse updateComment(String id, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.COMMENT_NOT_EXISTED));

        // Kiểm tra quyền (chỉ tác giả mới sửa được)
        String currentUserId = authenticationService.getCurrentUserId();
        if (!comment.getUser().getId().equals(currentUserId)) {
            throw new AppException(ErrorCodes.UNAUTHORIZED);
        }

        comment.setText(commentRequest.getText());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.COMMENT_NOT_EXISTED));

        String currentUserId = authenticationService.getCurrentUserId();
        boolean isAdmin = authenticationService.isAdmin();

        if (!comment.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new AppException(ErrorCodes.UNAUTHORIZED);
        }
        commentRepository.delete(comment);
    }

    // ===================== PRIVATE HELPER METHODS =====================

    private Comment findParentComment(String parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new AppException(ErrorCodes.COMMENT_NOT_EXISTED));
    }

    private void associateContent(Comment comment, CommentRequest request) {
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId())
                    .orElseThrow(() -> new AppException(ErrorCodes.MOVIE_NOT_EXISTED));
            comment.setMovie(movie);
        } else if (request.getSeriesId() != null) {
            Series series = seriesRepository.findById(request.getSeriesId())
                    .orElseThrow(() -> new AppException(ErrorCodes.SERIES_NOT_EXISTED));
            comment.setSeries(series);
        } else if (request.getEpisodeId() != null) {
            Episode episode = episodeRepository.findById(request.getEpisodeId())
                    .orElseThrow(() -> new AppException(ErrorCodes.EPISODE_NOT_EXISTED));
            comment.setEpisode(episode);
        } else {
            throw new IllegalArgumentException("Comment phải được liên kết với movie, series hoặc episode");
        }
    }

    private void broadcastComment(Comment comment) {
        CommentResponse response = commentMapper.toCommentResponse(comment);
        if (comment.getMovie() != null) {
            messagingTemplate.convertAndSend("/topic/movie/" + comment.getMovie().getId() + "/comments", response);
        } else if (comment.getSeries() != null) {
            messagingTemplate.convertAndSend("/topic/series/" + comment.getSeries().getId() + "/comments", response);
        } else if (comment.getEpisode() != null) {
            messagingTemplate.convertAndSend("/topic/episode/" + comment.getEpisode().getId() + "/comments", response);
        }
    }

    private void processNotifications(Comment comment) {
        if (comment.getParentComment() != null) {
            notificationService.notifyNewReply(comment);
        } else {
            notificationService.notifyNewComment(comment);
        }
    }
}
