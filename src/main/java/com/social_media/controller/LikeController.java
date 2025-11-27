package com.social_media.controller;

import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Like;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import com.social_media.service.LikeService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/likes")
@Validated
@AllArgsConstructor
public class LikeController {
    private final LikeService likeService;

    private final ToModelConverter<Like, LikeDto> likeToModelConverter;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeDto> createLike(
            Authentication authentication, @PathVariable UUID postId
    ) {
        User user = (User) authentication.getPrincipal();

        var like = likeToModelConverter.convertToModel(likeService.createLike(user, postId));

        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(
            Authentication authentication, @PathVariable UUID postId
    ) {
        User user = (User) authentication.getPrincipal();
        likeService.removeLike(user.getId(), postId);
    }
}
