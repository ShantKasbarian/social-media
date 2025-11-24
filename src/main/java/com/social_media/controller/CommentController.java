package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @PostMapping
    public ResponseEntity<CommentDto> comment(@RequestBody CommentDto commentDto, Authentication authentication) {
        return new ResponseEntity<>(
                commentConverter.convertToModel(
                        commentService.comment(
                                (User) authentication.getPrincipal(),
                                commentDto.postId(),
                                commentDto.comment()
                        )
                ), HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            Authentication authentication, @PathVariable UUID commentId
    ) {
        commentService.deleteComment((User) authentication.getPrincipal(), commentId);
    }

    @PutMapping
    public ResponseEntity<CommentDto> updateComment(
            @RequestBody CommentDto commentDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                commentConverter.convertToModel(
                        commentService.editComment(
                                (User) authentication.getPrincipal(), commentDto.id(),
                                commentDto.comment()
                        )
                )
        );
    }
}
