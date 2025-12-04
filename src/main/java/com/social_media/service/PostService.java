package com.social_media.service;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface PostService {
    Post createPost(User user, Post post);
    Post getPostById(@NotNull(message = "id must be specified") UUID id);
    Post updatePost(User user, @NotNull(message = "id must be specified") UUID id, String title);
    void deletePost(User user, @NotNull(message = "id must be specified") UUID id);
    Page<Post> getPostsByUserIdAcceptedFriendRequests(UUID id, Pageable pageable);
    Page<Post> getUserPosts(User user, @NotNull(message = "id must be specified") UUID userId, Pageable pageable);
    Page<Post> getUserLikedPosts(UUID id, Pageable pageable);
}
