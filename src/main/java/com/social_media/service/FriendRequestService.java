package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FriendRequestService extends Creatable<FriendRequest, UUID>, Deletable {
  FriendRequest updateStatus(User user, UUID requestId, FriendRequest.Status status);

  Page<FriendRequest> findByUserAndStatus(
      User user, FriendRequest.Status status, Pageable pageable);
}
