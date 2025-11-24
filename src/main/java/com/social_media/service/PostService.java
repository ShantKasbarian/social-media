package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Post createPost(Post post, User user);
    Post updatePost(String id, String title, User user);
    void deletePost(User user, String id);
    PageDto<Post, PostDto> getFriendsPosts(User user, Pageable pageable);
    PageDto<Post, PostDto> getUserPosts(String userId, Pageable pageable, User user);
    Post getPostById(String id, User user);
    Like likePost(String id, User user);
    void removeLike(String postId, User user);
    PageDto<Post, PostDto> getUserLikedPosts(User user, Pageable pageable);
    PageDto<Comment, CommentDto> getComments(User user, String postId, Pageable pageable);
}
