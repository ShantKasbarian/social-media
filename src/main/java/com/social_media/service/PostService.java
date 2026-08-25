package com.social_media.service;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService extends Creatable<Post, PostDto>, Updatable<Post, PostDto>, Deletable {
  Post findById(UUID id, User user);

  Page<Post> findByUserIdAcceptedFriendRequests(UUID id, Pageable pageable);

  Page<Post> findByUserId(User user, UUID userId, Pageable pageable);

  Page<Post> findLikedByUserId(UUID id, Pageable pageable);
}
