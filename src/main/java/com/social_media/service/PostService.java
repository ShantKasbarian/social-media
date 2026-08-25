package com.social_media.service;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
  Post createPost(User user, Post post);

  Post getPostById(UUID id, User user);

  Post updatePost(User user, PostDto postDto);

  void deletePost(User user, UUID id);

  Page<Post> getPostsByUserIdAcceptedFriendRequests(UUID id, Pageable pageable);

  Page<Post> getUserPosts(User user, UUID userId, Pageable pageable);

  Page<Post> getUserLikedPosts(UUID id, Pageable pageable);
}
