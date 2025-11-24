package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import org.springframework.data.domain.Pageable;

public interface FriendRequestService {
    FriendRequest addFriend(String targetUserId, User user);
    FriendRequest acceptFriend(String requestId, User user);
    PageDto<FriendRequest, FriendRequestDto> getFriends(User user, Pageable pageable);
    PageDto<FriendRequest, FriendRequestDto> getPendingUsers(User user, Pageable pageable);
    FriendRequest declineFriendRequest(String requestId, User user);
}
