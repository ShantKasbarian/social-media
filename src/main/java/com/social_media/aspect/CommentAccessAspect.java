package com.social_media.aspect;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.social_media.aspect.UserBlockAspect.BLOCKED_USER_MESSAGE;
import static com.social_media.service.impl.PostServiceImpl.POST_NOT_FOUND_MESSAGE;

@Aspect
@Component
@AllArgsConstructor
public class CommentAccessAspect {
    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Before(
            value = "execution(* com.social_media.controller.CommentController.getCommentsByPostId(..)) && args(postId,..)",
            argNames = "postId"
    )
    public void validate(Object postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Post post = postRepository.findById((UUID) postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.existsByUserIdTargetUserIdStatus(user.getId(), post.getUser().getId(), FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }
    }
}
