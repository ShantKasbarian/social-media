package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
@Slf4j
@AllArgsConstructor
public class CommentController {
    static final String TIME_PROPERTY = "time";

    private final CommentService commentService;

    private final ToModelConverter<Comment, CommentDto> commentToModelConverter;

    private final ToEntityConverter<Comment, CommentDto> commentDtoToEntityConverter;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            Authentication authentication, @RequestBody @Valid CommentDto commentDto
    ) {
        log.info("/comments with POST called, creating comment");

        User user = (User) authentication.getPrincipal();
        Comment comment = commentDtoToEntityConverter.convertToEntity(commentDto);

        var result = commentToModelConverter.convertToModel(
                commentService.createComment(user, comment)
        );

        log.info("created comment");

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(
            Authentication authentication, @RequestBody @Valid CommentDto commentDto
    ) {
        UUID id = commentDto.id();

        log.info("/comments with PUT called, updating comment with id {}", id);

        User user = (User) authentication.getPrincipal();

        var comment = commentToModelConverter.convertToModel(
                commentService.updateComment(user, id, commentDto.text())
        );

        log.info("updated comment with id {}", id);

        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(Authentication authentication, @PathVariable UUID id) {
        log.info("/comments/{} with DELETE called, deleting comment with the specified id", id);

        User user = (User) authentication.getPrincipal();

        commentService.deleteComment(user, id);

        log.info("deleted comment with id {}", id);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PageDto<Comment, CommentDto>>  getCommentsByPostId(
            Authentication authentication,
            @PathVariable UUID postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("/comments/posts/{} with GET called, fetching comments with the specified postId", postId);

        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

        var comments = new PageDto<>(
                commentService.getCommentsByPostId(user, postId, pageable),
                commentToModelConverter
        );

        log.info("fetched comments with postId {}", postId);

        return ResponseEntity.ok(comments);
    }
}
