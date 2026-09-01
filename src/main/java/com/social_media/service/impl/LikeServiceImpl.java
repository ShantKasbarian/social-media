package com.social_media.service.impl;

import static com.social_media.service.impl.PostServiceImpl.POST_NOT_FOUND_MESSAGE;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.LikeRepository;
import com.social_media.repository.PostRepository;
import com.social_media.repository.UserBlockRepository;
import com.social_media.service.LikeService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {
  private static final String TOO_MANY_LIKES_MESSAGE = "cannot like post more than once";

  private static final String LIKE_NOT_FOUND_MESSAGE = "liked not found";

  static final String BLOCKED_USER_MESSAGE = "cannot interact with or view blocked user posts";

  private final LikeRepository likeRepository;

  private final PostRepository postRepository;

  private final UserBlockRepository userBlockRepository;

  @Override
  @Transactional
  public Like create(User user, UUID data) {
    log.info("creating like for post with data {}", data);

    Post post =
        postRepository
            .findById(data)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    if (userBlockRepository.existsBlockBetween(user.getId(), post.getUser().getId())) {
      throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
    }

    if (likeRepository.existsByPostAndUser(post, user)) {
      throw new ResourceAlreadyExistsException(TOO_MANY_LIKES_MESSAGE);
    }

    Like like = new Like(user, post);

    likeRepository.save(like);

    log.info("created like for post with data {}", data);

    return like;
  }

  @Override
  @Transactional
  public void delete(User user, UUID postId) {
    UUID userId = user.getId();

    log.info("removing like for user with id {} and post with id {}", userId, postId);

    Like like =
        likeRepository
            .findByUserIdPostId(userId, postId)
            .orElseThrow(() -> new ResourceNotFoundException(LIKE_NOT_FOUND_MESSAGE));

    likeRepository.delete(like);

    log.info("removed like for user with id {} and post with id {}", userId, postId);
  }
}
