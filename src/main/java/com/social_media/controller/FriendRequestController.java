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

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    private final FriendRequestConverter friendRequestConverter;

    @PostMapping("/{friendId}")
    public ResponseEntity<FriendRequestDto> addFriend(Authentication authentication, @PathVariable UUID friendId) {
        return new ResponseEntity<>(
                friendRequestConverter.convertToModel(
                    friendRequestService.addFriend(
                            (User) authentication.getPrincipal(), friendId
                    )
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/request/{requestId}/accept")
    public ResponseEntity<FriendRequestDto> acceptFriend(Authentication authentication, @PathVariable UUID requestId) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                    friendRequestService.acceptFriend(
                            (User) authentication.getPrincipal(), requestId
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
                new PageDto<>(
                    friendRequestService.getFriends(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size)
                    ), friendRequestConverter
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

                new PageDto<>(
                    friendRequestService.getPendingUsers(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size)
                    ), friendRequestConverter
                )
        );
    }

    @PutMapping("/request/{requestId}/decline")
    public ResponseEntity<FriendRequestDto> declineFriendRequest(
            @PathVariable UUID requestId, Authentication authentication
    ) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                        friendRequestService.declineFriendRequest(
                            (User) authentication.getPrincipal(),
                            requestId
                        )
                )
        );
    }

}
