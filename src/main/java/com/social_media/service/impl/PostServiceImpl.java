package com.social_media.service.impl;

import com.social_media.entity.*;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.*;
import com.social_media.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE = "unable to delete or modify post";

    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String BLOCKED_USER_MESSAGE = "you have blocked or have been blocked by this user";

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final LikeRepository likeRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Post createPost(User user, Post post) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setPostedTime(LocalDateTime.now());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(User user, UUID id, String title) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
        }

        if (title == null || title.isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setTitle(title);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(User user, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
        }

        postRepository.delete(post);
    }

    @Override
    public Page<Post> getFriendsPosts(User user, Pageable pageable) {
        return postRepository.findByUser_Friends(user.getId(), pageable);
    }

    @Override
    public Page<Post> getUserPosts(User user, UUID userId, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(user.getId(), userId)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        return postRepository.findByUser(targetUser, pageable);
    }

    @Override
    public Post getPostById(User user, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(user.getId(), post.getUser().getId())) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }
        return post;
    }

    @Override
    @Transactional
    public Like likePost(User user, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        FriendRequest friendRequest = friendRequestRepository
                .findByUserIdFriendId(user.getId(), post.getUser().getId())
                .orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException("user has blocked you");
        }

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException("cannot like post more than once");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        return likeRepository.save(like);
    }

    @Override
    @Transactional
    public void removeLike(User user, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException("you haven't liked this post"));

        likeRepository.delete(like);
    }

    @Override
    public Page<Post> getUserLikedPosts(User user, Pageable pageable) {
        return postRepository.findByLikesUser(user, pageable);
    }

    @Override
    public Page<Comment> getComments(User user, UUID postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(user.getId(), post.getUser().getId())) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        return commentRepository.findByPostId(postId, pageable);
    }
}
