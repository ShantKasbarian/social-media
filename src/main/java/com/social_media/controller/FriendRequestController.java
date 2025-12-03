package com.social_media.controller;

import com.social_media.converter.ToModelConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.service.FriendRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/friend-requests")
@Slf4j
@AllArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    private final ToModelConverter<FriendRequest, FriendRequestDto> friendRequestToModelConverter;

    @PostMapping("/users/{targetUserId}")
    public ResponseEntity<FriendRequestDto> createFriendRequest(
            Authentication authentication, @PathVariable UUID targetUserId
    ) {
        log.info("/friend-requests/users/{} with POST called, creating friendRequest with the specified targetUserId", targetUserId);

        User user = (User) authentication.getPrincipal();

        FriendRequestDto friendRequestDto = friendRequestToModelConverter.convertToModel(
                friendRequestService.createFriendRequest(user, targetUserId)
        );

        log.info("created friend request with targetUserId {}", targetUserId);

        return new ResponseEntity<>(friendRequestDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<FriendRequestDto> updateFriendRequestStatus(
            Authentication authentication, @PathVariable UUID id, @PathVariable FriendRequest.Status status
    ) {
        log.info("/friend-requests/{}/status/{} with PATCH called, updating friendRequest with specified id to specified status", id, status);

        User user = (User) authentication.getPrincipal();

        FriendRequestDto friendRequestDto = friendRequestToModelConverter.convertToModel(
                friendRequestService.updateFriendRequestStatus(user, id, status)
        );

        log.info("updated friendRequest status with id {} and status {}", id, status);

        return ResponseEntity.ok(friendRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendRequest(
            Authentication authentication, @PathVariable UUID id
    ) {
        log.info("/friend-requests/{} with DELETE called, deleting friendRequest with the specified id", id);

        User user = (User) authentication.getPrincipal();

        friendRequestService.deleteFriendRequest(user, id);

        log.info("deleted friendRequest with id {}", id);
    }

    @GetMapping("/{status}")
    public ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> getFriendRequestsByStatus(
            Authentication authentication,
            @PathVariable FriendRequest.Status status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        UUID id = user.getId();
        Pageable pageable = PageRequest.of(page, size);

        log.info("/friend-requests/{} with GET called, fetching friend-requests of user with id {} and specified status", status,id);

        Page<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByUserStatus(user, status, pageable);

        var pageDto = new PageDto<>(friendRequests, friendRequestToModelConverter);

        log.info("fetched friend-requests of user with id {} and status {}", id, status);

        return ResponseEntity.ok(pageDto);
    }
}
