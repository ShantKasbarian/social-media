package com.social_media.controller;

import com.social_media.converter.LikeConverter;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import com.social_media.service.LikeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/likes")
public class LikeController {
  private final LikeService likeService;

  private final LikeConverter likeConverter;

  @PostMapping("/posts/{postId}")
  public ResponseEntity<LikeDto> createLike(
      Authentication authentication, @PathVariable UUID postId) {
    User user = (User) authentication.getPrincipal();

    var like = likeService.createLike(user, postId);
    var likeDto = likeConverter.convertToModel(like);

    return new ResponseEntity<>(likeDto, HttpStatus.CREATED);
  }

  @DeleteMapping("/posts/{postId}")
  public ResponseEntity<Void> deleteLike(Authentication authentication, @PathVariable UUID postId) {
    User user = (User) authentication.getPrincipal();

    likeService.deleteLikeByPostId(user.getId(), postId);

    return ResponseEntity.noContent().build();
  }
}
