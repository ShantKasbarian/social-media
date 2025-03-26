package com.social_media.controllers;

import com.social_media.converters.FriendConverter;
import com.social_media.entities.User;
import com.social_media.models.FriendDto;
import com.social_media.models.PageDto;
import com.social_media.services.FriendService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;

    private final FriendConverter friendConverter;

    @PostMapping("/{friendId}")
    public FriendDto addFriend(Authentication authentication, @PathVariable String friendId) {
        return friendConverter.convertToModel(
                friendService.addFriend(
                        friendId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @PutMapping("/request/{requestId}/accept")
    public FriendDto acceptFriend(Authentication authentication, @PathVariable String requestId) {
        return friendConverter.convertToModel(
                friendService.acceptFriend(
                        requestId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @PutMapping("/request/{requestId}/block")
    public FriendDto blockFriend(Authentication authentication, @PathVariable String requestId) {
        return friendConverter.convertToModel(
                friendService.blockFriend(
                        requestId,
                        (User) authentication.getPrincipal()
                )
        );
    }

    @GetMapping
    public PageDto<FriendDto> getFriends(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendService.getFriends(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/blocked")
    public PageDto<FriendDto> getBlockedUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendService.getBlockedUsers(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/pending")
    public PageDto<FriendDto> getPendingUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return friendService.getPendingUsers(
                (User) authentication.getPrincipal(),
                PageRequest.of(page, size)
        );
    }
}
