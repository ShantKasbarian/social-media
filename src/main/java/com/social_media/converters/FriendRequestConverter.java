package com.social_media.converters;

import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.models.FriendRequestDto;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestConverter implements Converter<FriendRequest, FriendRequestDto> {
    @Override
    public FriendRequest convertToEntity(FriendRequestDto model) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(model.id());
        friendRequest.setStatus(FriendshipStatus.valueOf(model.status()));
        return friendRequest;
    }

    @Override
    public FriendRequestDto convertToModel(FriendRequest entity) {
        User user = entity.getUser();
        User friend = entity.getFriend();

        String blockerId = null;
        if (entity.getBlocker() != null) {
            blockerId = entity.getBlocker().getId();
        }

        return new FriendRequestDto(
                entity.getId(),
                user.getId(),
                user.getUsername(),
                friend.getId(),
                friend.getUsername(),
                entity.getStatus().toString(),
                blockerId
        );
    }
}
