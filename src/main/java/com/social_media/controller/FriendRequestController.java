package com.social_media.controller;

import com.social_media.converter.ToModelConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.service.FriendRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/friend-requests")
@AllArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    private final ToModelConverter<FriendRequest, FriendRequestDto> friendRequestToModelConverter;

    @PostMapping("/users/{targetUserId}")
    public ResponseEntity<FriendRequestDto> createFriendRequest(
            Authentication authentication, @PathVariable UUID targetUserId
    ) {
        User user = (User) authentication.getPrincipal();

        var friendRequest = friendRequestToModelConverter.convertToModel(
                friendRequestService.createFriendRequest(user, targetUserId)
        );

        return new ResponseEntity<>(friendRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<FriendRequestDto> updateFriendRequestStatus(
            Authentication authentication, @PathVariable UUID id, @PathVariable FriendRequest.Status status
    ) {
        User user = (User) authentication.getPrincipal();

        var friendRequest = friendRequestToModelConverter.convertToModel(
                friendRequestService.updateFriendRequestStatus(user, id, status)
        );

        return ResponseEntity.ok(friendRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendRequest(
            Authentication authentication, @PathVariable UUID requestId
    ) {
        User user = (User) authentication.getPrincipal();
        friendRequestService.deleteFriendRequest(user, requestId);
    }

    @GetMapping("/{status}")
    public ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> getFriendRequestsByStatus(
            Authentication authentication,
            @PathVariable FriendRequest.Status status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();

        var friendRequests = new PageDto<>(
                friendRequestService.getFriendRequestsByStatus(
                        user, status, PageRequest.of(page, size)
                ),
                friendRequestToModelConverter
        );

        return ResponseEntity.ok(friendRequests);
    }
}
