package com.social_media.aspect;

import com.social_media.entity.Comment;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

import static com.social_media.service.impl.PostServiceImpl.POST_NOT_FOUND_MESSAGE;

@Aspect
@Component
@AllArgsConstructor
public class UserBlockAspect {
    private static final String CREATE_COMMENT_METHOD_NAME = "createComment";

    private static final String CREATE_LIKE_METHOD_NAME = "createLike";

    private static final String GET_USER_POSTS_METHOD_NAME = "getUserPosts";

    static final String BLOCKED_USER_MESSAGE = "user is blocked";

    private final FriendRequestRepository friendRequestRepository;

    private final PostRepository postRepository;

    @Before("@annotation(com.social_media.annotation.ValidateUserNotBlocked)")
    public void validate(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        if (isFriendRequestStatusBlocked(method, args)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }
    }

    private boolean isFriendRequestStatusBlocked(Method method, Object[] args) {
        return switch (method.getName()) {
            case CREATE_COMMENT_METHOD_NAME -> checkCreateComment(args);

            case CREATE_LIKE_METHOD_NAME -> checkCreateLike(args);

            case GET_USER_POSTS_METHOD_NAME -> checkGetUserPosts(args);

            default -> false;
        };
    }

    private boolean checkCreateComment(Object[] args) {
        User user = null;
        Comment comment = null;

        for (Object object: args) {
            if (object instanceof User) {
                user = (User) object;
            }

            if (object instanceof Comment) {
                comment = (Comment) object;
            }
        }

        return friendRequestRepository.existsByUserIdTargetUserId(user.getId(), comment.getPost().getUser().getId());
    }

    private boolean checkCreateLike(Object[] args) {
        User user = null;
        UUID postId = null;

        for (Object object: args) {
            if (object instanceof User) {
                user = (User) object;
            }

            if (object instanceof UUID) {
                postId = (UUID) object;
            }
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        return friendRequestRepository.existsByUserIdTargetUserIdStatus(user.getId(), post.getUser().getId(), FriendRequest.Status.BLOCKED);

    }

    private boolean checkGetUserPosts(Object[] args) {
        User user = null;
        UUID targetUserId = null;

        for (Object object: args) {
            if (object instanceof User) {
                user = (User) object;
            }

            if (object instanceof UUID) {
                targetUserId = (UUID) object;
            }
        }

        return friendRequestRepository.existsByUserIdTargetUserIdStatus(user.getId(), targetUserId, FriendRequest.Status.BLOCKED);
    }
}
