package com.social_media.service.impl;

import static com.social_media.service.impl.LikeServiceImpl.BLOCKED_USER_MESSAGE;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.PostDto;
import com.social_media.repository.*;
import com.social_media.service.PostService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class PostServiceImpl implements PostService {
  public static final String POST_NOT_FOUND_MESSAGE = "post not found";

  private static final String UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE =
      "unable to delete or modify post";

  private final PostRepository postRepository;

  private final UserBlockRepository userBlockRepository;

  @Override
  @Transactional
  public Post createPost(User user, Post post) {
    UUID id = user.getId();

    log.info("creating post for user with id {}", id);

    post.setTime(Instant.now());
    post.setUser(user);

    postRepository.save(post);

    log.info("created post for user with id {}", id);

    return post;
  }

  @Override
  public Post getPostById(UUID id, User user) {
    log.info("fetching post with id {}", id);

    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    UUID userId = user.getId();
    UUID authorId = post.getUser().getId();

    if (!userId.equals(authorId) && userBlockRepository.existsBlockBetween(userId, authorId)) {
      throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
    }

    log.info("fetched post with id {}", id);

    return post;
  }

  @Override
  @Transactional
  public Post updatePost(User user, PostDto postDto) {
    UUID id = postDto.id();

    log.info("updating post with id {}", id);

    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
    }

    post.setText(postDto.text());

    log.info("updated post with id {}", id);

    return post;
  }

  @Override
  @Transactional
  public void deletePost(User user, UUID id) {
    log.info("deleting post with id {}", id);

    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
    }

    postRepository.delete(post);

    log.info("deleted post with id {}", id);
  }

  @Override
  public Page<Post> getPostsByUserIdAcceptedFriendRequests(UUID id, Pageable pageable) {
    log.info("fetching friends posts of user with id {}", id);

    Page<Post> posts = postRepository.findByUserIdAcceptedFriendRequests(id, pageable);

    log.info("fetched friends posts of user with id {}", id);

    return posts;
  }

  @Override
  public Page<Post> getUserPosts(User user, UUID userId, Pageable pageable) {
    log.info("fetching posts of user with id {}", userId);

    UUID currentUserId = user.getId();

    if (!currentUserId.equals(userId)
        && userBlockRepository.existsBlockBetween(currentUserId, userId)) {
      throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
    }

    Page<Post> posts = postRepository.findByUserId(userId, pageable);

    log.info("fetching posts of user with id {}", userId);

    return posts;
  }

  @Override
  public Page<Post> getUserLikedPosts(UUID id, Pageable pageable) {
    log.info("fetching liked posts of user with id {}", id);

    Page<Post> posts = postRepository.findByUserIdLikes(id, pageable);

    log.info("fetched liked posts of user with id {}", id);

    return posts;
  }
}
