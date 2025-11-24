package com.social_media.service.impl;

import com.social_media.converter.CommentConverter;
import com.social_media.converter.PostConverter;
import com.social_media.entity.*;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import com.social_media.repository.*;
import com.social_media.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostConverter postConverter;

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final LikeRepository likeRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Post createPost(Post post, User user) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setId(UUID.randomUUID().toString());
        post.setPostedTime(LocalDateTime.now());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(String id, String title, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot modify the post of another user");
        }

        if (title == null || title.isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setTitle(title);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(User user, String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot delete the post of another user");
        }

        postRepository.delete(post);
    }

    @Override
    public PageDto<Post, PostDto> getFriendsPosts(User user, Pageable pageable) {
        return new PageDto<>(
                postRepository.findByUser_Friends(user.getId(), pageable),
                postConverter
        );
    }

    @Override
    public PageDto<Post, PostDto> getUserPosts(String userId, Pageable pageable, User user) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        List<User> currentUserBlockedUsers = user.getBlockedUsers();

        for(User blockedUser: currentUserBlockedUsers) {
            if (blockedUser.getId().equals(userId)) {
                throw new RequestNotAllowedException("you have blocked this user");
            }
        }

        List<User> targetUserBlockedUsers = targetUser.getBlockedUsers();
        String currentUserId = user.getId();

        for (User blockedUser: targetUserBlockedUsers) {
            if (blockedUser.getId().equals(currentUserId)) {
                throw new RequestNotAllowedException("you have blocked this user");
            }
        }

        return new PageDto<>(
                postRepository.findByUser(
                        userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("user not found")),
                        pageable
                ),
                postConverter
        );
    }

    @Override
    public Post getPostById(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        User postUser = post.getUser();
        List<User> postUserBlockedUsers = postUser.getBlockedUsers();
        String currentUserId = postUser.getId();

        for(User blockedUser: postUserBlockedUsers) {
            if (blockedUser.getId().equals(currentUserId)) {
                throw new RequestNotAllowedException("you have blocked this user");
            }
        }

        List<User> currentUserBlockedUsers = user.getBlockedUsers();
        String postUserId = postUser.getId();
        for(User blockedUser: currentUserBlockedUsers) {
            if (blockedUser.getId().equals(postUserId)) {
                throw new RequestNotAllowedException("you have blocked this user");
            }
        }

        return post;
    }

    @Override
    @Transactional
    public Like likePost(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        FriendRequest friendRequest = friendRequestRepository
                .findByUser_idFriend_id(user.getId(), post.getUser().getId())
                .orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendshipStatus.BLOCKED)) {
            throw new RequestNotAllowedException("user has blocked you");
        }

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException("cannot like post more than once");
        }

        return likeRepository.save(new Like(UUID.randomUUID().toString(), user, post));
    }

    @Override
    @Transactional
    public void removeLike(String postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        likeRepository.delete(
                likeRepository.findByPostAndUser(post, user)
                        .orElseThrow(() -> new ResourceNotFoundException("you haven't liked this post"))
        );
    }

    @Override
    public PageDto<Post, PostDto> getUserLikedPosts(User user, Pageable pageable) {
        return new PageDto<>(
                postRepository.findByLikesUser(user, pageable),
                postConverter
        );
    }

    @Override
    public PageDto<Comment, CommentDto> getComments(User user, String postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        FriendRequest friendRequest = friendRequestRepository
                .findByUser_idFriend_id(user.getId(), post.getUser().getId())
                .orElse(null);

        if (
                friendRequest != null &&
                friendRequest.getStatus().equals(FriendshipStatus.BLOCKED)
        ) {
            throw new RequestNotAllowedException("you have been blocked by this user");
        }

        return new PageDto<>(
                commentRepository.findByPost_id(postId, pageable),
                commentConverter
        );
    }
}
