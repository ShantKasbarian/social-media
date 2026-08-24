package com.social_media.controller;

import com.social_media.converter.UserBlockConverter;
import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.model.PageDto;
import com.social_media.model.UserBlockDto;
import com.social_media.service.UserBlockService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user-blocks")
public class UserBlockController {
  private final UserBlockService userBlockService;

  private final UserBlockConverter userBlockConverter;

  @PostMapping("/{userId}")
  public ResponseEntity<UserBlockDto> blockPost(
      @AuthenticationPrincipal Authentication authentication, @PathVariable UUID userId) {
    log.info("POST /user-blocks/{}, blocking user with id {}", userId, userId);

    User user = (User) authentication.getPrincipal();

    UserBlockDto userBlockDto =
        userBlockConverter.convertToModel(userBlockService.createUserBlock(user, userId));

    log.info("POST /user-blocks/{}, blocked user with id {}", userId, userId);

    return new ResponseEntity<>(userBlockDto, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<PageDto<UserBlock, UserBlockDto>> getUserBlocks(
      @AuthenticationPrincipal Authentication authentication,
      @RequestParam int page,
      @RequestParam int size) {
    UUID userId = ((User) authentication.getPrincipal()).getId();

    log.info(
        "GET /user-blocks with page {} and size {}, fetching blocked users of user with id {}",
        page,
        size,
        userId);

    var userBlocks = userBlockService.getUserBlocksByUserId(userId, PageRequest.of(page, size));
    var pageDto = new PageDto<>(userBlocks, userBlockConverter);

    log.info(
        "GET /user-blocks with page {} and size {}, fetched blocked users of user with id {}",
        page,
        size,
        userId);

    return ResponseEntity.ok(pageDto);
  }
}
