package com.social_media.controllers;

import com.social_media.converters.FriendRequestConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.User;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.services.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    private final FriendRequestConverter friendRequestConverter;

    @PostMapping("/{friendId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendRequestDto addFriend(Authentication authentication, @PathVariable String friendId) {
        return friendRequestConverter.convertToModel(
                friendRequestService.addFriend(
                        friendId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @PutMapping("/request/{requestId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public FriendRequestDto acceptFriend(Authentication authentication, @PathVariable String requestId) {
        return friendRequestConverter.convertToModel(
                friendRequestService.acceptFriend(
                        requestId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @PutMapping("/request/{requestId}/block")
    @ResponseStatus(HttpStatus.OK)
    public FriendRequestDto blockFriend(Authentication authentication, @PathVariable String requestId) {
        return friendRequestConverter.convertToModel(
                friendRequestService.blockFriend(
                        requestId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @PutMapping("/request/{requestId}/unblock")
    @ResponseStatus(HttpStatus.OK)
    public FriendRequestDto unblockFriend(Authentication authentication, @PathVariable String requestId) {
        return friendRequestConverter.convertToModel(
                friendRequestService.unblockFriend(
                        requestId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @GetMapping
    public PageDto<FriendRequest, FriendRequestDto> getFriends(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendRequestService.getFriends(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/blocked")
    public PageDto<FriendRequest, FriendRequestDto> getBlockedUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendRequestService.getBlockedUsers(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/pending")
    public PageDto<FriendRequest, FriendRequestDto> getPendingUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendRequestService.getPendingUsers(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }
}
