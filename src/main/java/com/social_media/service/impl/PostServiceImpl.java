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
    private static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE = "unable to delete or modify post";

    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String BLOCKED_USER_MESSAGE = "you have blocked or have been blocked by this user";

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
    public void deletePost(User user, String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
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
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        String currentUserId = user.getId();

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(currentUserId, userId)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        return new PageDto<>(
                postRepository.findByUser(targetUser, pageable), postConverter
        );
    }

    @Override
    public Post getPostById(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(user.getId(), post.getUser().getId())) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }
        return post;
    }

    @Override
    @Transactional
    public Like likePost(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        FriendRequest friendRequest = friendRequestRepository
                .findByUserIdFriendId(user.getId(), post.getUser().getId())
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
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

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
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(user.getId(), post.getUser().getId())) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        return new PageDto<>(
                commentRepository.findByPostId(postId, pageable), commentConverter
        );
    }
}
