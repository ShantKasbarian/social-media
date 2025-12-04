package com.social_media.controller;

import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Like;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import com.social_media.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/likes")
@Validated
@Slf4j
@AllArgsConstructor
public class LikeController {
    private final LikeService likeService;

    private final ToModelConverter<Like, LikeDto> likeToModelConverter;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeDto> createLike(
            Authentication authentication, @PathVariable UUID postId
    ) {
        log.info("/likes/posts/{} with POST called, creating like for post with the specified postId", postId);

        User user = (User) authentication.getPrincipal();

        var like = likeToModelConverter.convertToModel(likeService.createLike(user, postId));

        log.info("created like for post with id {}", postId);

        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(Authentication authentication, @PathVariable UUID postId) {
        User user = (User) authentication.getPrincipal();
        UUID userId = user.getId();

        log.info("/likes/posts/{} with DELETE called, deleting like for post with the specified postId and user with id {}", postId, userId);

        likeService.deleteLikeByPostId(userId, postId);

        log.info("deleted like for post with id {} and user with id {}", postId, userId);
    }
}
