package com.social_media.controller;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.service.FriendRequestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {
  private final FriendRequestService friendRequestService;

  private final FriendRequestConverter friendRequestConverter;

  @PostMapping("/users/{targetUserId}")
  public ResponseEntity<FriendRequestDto> createFriendRequest(
      Authentication authentication, @PathVariable UUID targetUserId) {
    User user = (User) authentication.getPrincipal();

    FriendRequest friendRequest = friendRequestService.create(user, targetUserId);
    FriendRequestDto friendRequestDto = friendRequestConverter.convertToModel(friendRequest);

    return new ResponseEntity<>(friendRequestDto, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}/status/{status}")
  public ResponseEntity<FriendRequestDto> updateFriendRequestStatus(
      Authentication authentication,
      @PathVariable UUID id,
      @PathVariable FriendRequest.Status status) {
    User user = (User) authentication.getPrincipal();

    FriendRequest friendRequest = friendRequestService.updateStatus(user, id, status);
    FriendRequestDto friendRequestDto = friendRequestConverter.convertToModel(friendRequest);

    return ResponseEntity.ok(friendRequestDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFriendRequest(
      Authentication authentication, @PathVariable UUID id) {
    User user = (User) authentication.getPrincipal();

    friendRequestService.delete(user, id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{status}")
  public ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> getFriendRequestsByStatus(
      Authentication authentication,
      @PathVariable FriendRequest.Status status,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    User user = (User) authentication.getPrincipal();
    Pageable pageable = PageRequest.of(page, size);

    var friendRequests = friendRequestService.findByUserAndStatus(user, status, pageable);
    var pageDto = new PageDto<>(friendRequests, friendRequestConverter);

    return ResponseEntity.ok(pageDto);
  }
}
