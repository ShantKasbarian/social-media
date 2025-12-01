package com.social_media.aspect;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.model.PostDto;
import com.social_media.repository.FriendRequestRepository;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.social_media.aspect.UserBlockAspect.BLOCKED_USER_MESSAGE;

@Aspect
@Component
@AllArgsConstructor
public class PostAccessAspect {
    private final FriendRequestRepository friendRequestRepository;

    @AfterReturning(
            pointcut = "execution(* com.social_media.controller.PostController.getPostById(..))",
            returning = "response"
    )
    public void validate(Object response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        var responseEntity = (ResponseEntity<?>) response;
        var postDto = (PostDto) responseEntity.getBody();

        if (friendRequestRepository.existsByUserIdTargetUserIdStatus(user.getId(), postDto.userId(), FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }
    }
}
