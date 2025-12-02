package com.social_media.converter;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestConverter implements ToModelConverter<FriendRequest, FriendRequestDto> {
    @Override
    public FriendRequestDto convertToModel(FriendRequest entity) {
        User user = entity.getUser();
        User targetUser = entity.getTargetUser();

        return new FriendRequestDto(
                entity.getId(),
                user.getId(),
                user.getUsername(),
                targetUser.getId(),
                targetUser.getUsername(),
                entity.getStatus()
        );
    }
}
