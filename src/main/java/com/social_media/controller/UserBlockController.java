package com.social_media.controller;

import com.social_media.converter.UserBlockConverter;
import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.model.PageDto;
import com.social_media.model.UserBlockDto;
import com.social_media.service.UserBlockService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user-blocks")
public class UserBlockController {
  private final UserBlockService userBlockService;

  private final UserBlockConverter userBlockConverter;

  @PostMapping("/{userId}")
  public ResponseEntity<UserBlockDto> createUserBlock(
      Authentication authentication, @PathVariable UUID userId) {
    User user = (User) authentication.getPrincipal();

    UserBlock userBlock = userBlockService.create(user, userId);
    UserBlockDto userBlockDto = userBlockConverter.convertToModel(userBlock);

    return new ResponseEntity<>(userBlockDto, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<PageDto<UserBlock, UserBlockDto>> getUserBlocksByUserId(
      Authentication authentication,
      @RequestParam(defaultValue = "0", required = false) int page,
      @RequestParam(defaultValue = "10", required = false) int size) {
    UUID userId = ((User) authentication.getPrincipal()).getId();

    var userBlocks = userBlockService.findByUserId(userId, PageRequest.of(page, size));
    var pageDto = new PageDto<>(userBlocks, userBlockConverter);

    return ResponseEntity.ok(pageDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserBlock(
      Authentication authentication, @PathVariable UUID id) {
    User user = (User) authentication.getPrincipal();

    userBlockService.delete(user, id);

    return ResponseEntity.noContent().build();
  }
}
