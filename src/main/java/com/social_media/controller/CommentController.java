package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.service.CommentService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class CommentController {
    private static final String TIME_PROPERTY = "time";

    private final CommentService commentService;

    private final ToModelConverter<Comment, CommentDto> commentToModelConverter;

    private final ToEntityConverter<Comment, CommentDto> commentDtoToEntityConverter;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            Authentication authentication, @RequestBody CommentDto commentDto
    ) {
        User user = (User) authentication.getPrincipal();

        var comment = commentToModelConverter.convertToModel(
                commentService.createComment(
                        user, commentDtoToEntityConverter.convertToEntity(commentDto)
                )
        );

        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(
            Authentication authentication, @RequestBody CommentDto commentDto
    ) {
        User user = (User) authentication.getPrincipal();

        var comment = commentToModelConverter.convertToModel(
                commentService.updateComment(
                    user, commentDtoToEntityConverter.convertToEntity(commentDto)
                )
        );

        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            Authentication authentication, @PathVariable UUID commentId
    ) {
        commentService.deleteComment((User) authentication.getPrincipal(), commentId);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PageDto<Comment, CommentDto>>  getCommentsByPostId(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

        var comments = new PageDto<>(
                commentService.getCommentsByPostId(user, id, pageable),
                commentToModelConverter
        );

        return ResponseEntity.ok(comments);
    }
}
