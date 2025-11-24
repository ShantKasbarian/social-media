package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {
    Post createPost(User user, Post post);
    Post updatePost(User user, UUID id, String title);
    void deletePost(User user, UUID id);
    Page<Post> getFriendsPosts(User user, Pageable pageable);
    Page<Post> getUserPosts(User user, UUID userId, Pageable pageable);
    Post getPostById(User user, UUID id);
    Like likePost(User user, UUID id);
    void removeLike(User user, UUID postId);
    Page<Post> getUserLikedPosts(User user, Pageable pageable);
    Page<Comment> getComments(User user, UUID postId, Pageable pageable);
}
