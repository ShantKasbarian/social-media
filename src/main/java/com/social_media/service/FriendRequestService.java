package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRequestService extends Creatable<FriendRequest, UUID>, Deletable {
  FriendRequest acceptFriendRequest(User user, UUID requestId);

  Page<FriendRequest> findByUserAndStatus(
      User user, FriendRequest.Status status, Pageable pageable);
}
