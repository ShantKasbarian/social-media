package com.social_media.controller;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    private final FriendRequestConverter friendRequestConverter;

    @PostMapping("/{friendId}")
    public ResponseEntity<FriendRequestDto> addFriend(Authentication authentication, @PathVariable String friendId) {
        return new ResponseEntity<>(
                friendRequestConverter.convertToModel(
                    friendRequestService.addFriend(
                            friendId,
                            (User) authentication.getPrincipal()
                    )
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/request/{requestId}/accept")
    public ResponseEntity<FriendRequestDto> acceptFriend(Authentication authentication, @PathVariable String requestId) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                    friendRequestService.acceptFriend(
                            requestId,
                            (User) authentication.getPrincipal()
                    )
                )
        );
    }

    @GetMapping
    public ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> getFriends(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                friendRequestService.getFriends(
                    (User) authentication.getPrincipal(),
                    PageRequest.of(page, size)
                )
        );
    }


    @GetMapping("/pending")
    public ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> getPendingUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                friendRequestService.getPendingUsers(
                    (User) authentication.getPrincipal(),
                    PageRequest.of(page, size)
                )
        );
    }

    @PutMapping("/request/{requestId}/decline")
    public ResponseEntity<FriendRequestDto> declineFriendRequest(
            @PathVariable String requestId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                        friendRequestService.declineFriendRequest(
                            requestId,
                            (User) authentication.getPrincipal()
                        )
                )
        );
    }

}
