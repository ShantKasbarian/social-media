package com.social_media.service.impl;

import com.social_media.annotation.ValidateUserNotBlocked;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.LikeRepository;
import com.social_media.repository.PostRepository;
import com.social_media.service.LikeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.social_media.service.impl.PostServiceImpl.POST_NOT_FOUND_MESSAGE;

@Service
@Slf4j
@AllArgsConstructor
public class LikeServiceImpl implements LikeService {
    private static final String TOO_MANY_LIKES_MESSAGE = "cannot like post more than once";

    private static final String LIKE_NOT_FOUND_MESSAGE = "liked not found";

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    @Override
    @Transactional
    @ValidateUserNotBlocked
    public Like createLike(User user, UUID id) {
        log.info("creating like for post with id {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException(TOO_MANY_LIKES_MESSAGE);
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);

        log.info("created like for post with id {}", id);

        return like;
    }

    @Override
    @Transactional
    public void deleteLikeByPostId(UUID userId, UUID postId) {
        log.info("removing like for user with id {} and post with id {}", userId, postId);

        Like like = likeRepository.findByUserIdPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException(LIKE_NOT_FOUND_MESSAGE));

        likeRepository.delete(like);

        log.info("removed like for user with id {} and post with id {}", userId, postId);
    }
}
