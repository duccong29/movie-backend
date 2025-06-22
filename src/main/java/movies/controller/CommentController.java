package movies.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import movies.dto.request.Comment.CommentRequest;
import movies.dto.response.ApiResponse;
import movies.dto.response.Comment.CommentResponse;
import movies.dto.response.PageResponse;
import movies.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(commentRequest);
        return ApiResponse.<CommentResponse>builder()
                .data(commentResponse)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CommentResponse> getComment(@PathVariable String id) {
        CommentResponse commentResponse = commentService.getCommentById(id);
        return ApiResponse.<CommentResponse>builder()
                .data(commentResponse)
                .build();
    }

    @GetMapping("/movie/{movieId}")
    public ApiResponse<PageResponse<CommentResponse>> getMovieComments(
            @PathVariable String movieId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        PageResponse<CommentResponse> comments = commentService.getCommentsByMovieId(movieId, page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(comments)
                .build();
    }

    @GetMapping("/series/{seriesId}")
    public ApiResponse<PageResponse<CommentResponse>> getSeriesComments(
            @PathVariable String seriesId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        PageResponse<CommentResponse> comments = commentService.getCommentsBySeriesId(seriesId, page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(comments)
                .build();
    }

    @GetMapping("/episode/{episodeId}")
    public ApiResponse<PageResponse<CommentResponse>> getEpisodeComments(
            @PathVariable String episodeId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        PageResponse<CommentResponse> comments = commentService.getCommentsByEpisodeId(episodeId, page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(comments)
                .build();
    }

    @GetMapping("/my-comments")
    public ApiResponse<PageResponse<CommentResponse>> getMyComments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size) {
        PageResponse<CommentResponse> comments = commentService.getMyComments(page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(comments)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<CommentResponse>> getAllComments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        PageResponse<CommentResponse> comments = commentService.getAllComments(page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(comments)
                .build();
    }

    @GetMapping("/{id}/replies")
    public ApiResponse<PageResponse<CommentResponse>> getReplies(
            @PathVariable String id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageResponse<CommentResponse> replies = commentService.getCommentReplies(id, page, size);
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .data(replies)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.updateComment(id, commentRequest);
        return ApiResponse.<CommentResponse>builder()
                .data(commentResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ApiResponse<String> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ApiResponse.<String>builder()
                .data("Comment has been deleted")
                .build();
    }
}
