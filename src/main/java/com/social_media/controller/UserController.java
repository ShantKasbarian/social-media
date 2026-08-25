package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import com.social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
  private static final String USERNAME_SORT_PROPERTY = "username";

  private final UserService userService;

  private final UserConverter userConverter;

  @GetMapping
  public ResponseEntity<UserDto> getProfile(Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    UserDto userDto = userConverter.convertToModel(user);

    return ResponseEntity.ok(userDto);
  }

  @PutMapping
  public ResponseEntity<Void> updateUser(
      Authentication authentication, @RequestBody UserDto userDto) {
    User user = (User) authentication.getPrincipal();

    userService.update(user, userDto);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{username}")
  public ResponseEntity<PageDto<User, UserDto>> searchByUsername(
      @PathVariable String username,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(USERNAME_SORT_PROPERTY)));

    var users = userService.findByUsername(username, pageable);
    var pageDto = new PageDto<>(users, userConverter);

    return ResponseEntity.ok(pageDto);
  }
}
