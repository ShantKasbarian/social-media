package com.social_media.service;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    Post createPost(User user, Post post);
    Post getPostById(UUID id);
    Post updatePost(User user, UUID id, String title);
    void deletePost(User user, UUID id);
    Page<Post> getFriendsPosts(User user, Pageable pageable);
    Page<Post> getUserPosts(User user, UUID userId, Pageable pageable);
    Page<Post> getUserLikedPosts(User user, Pageable pageable);
}
