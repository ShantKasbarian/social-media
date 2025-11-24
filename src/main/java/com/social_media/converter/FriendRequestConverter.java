package com.social_media.converter;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.FriendshipStatus;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestConverter implements
        ToEntityConverter<FriendRequest, FriendRequestDto>,
        ToModelConverter<FriendRequest, FriendRequestDto> {

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

        return new FriendRequestDto(
                entity.getId(),
                user.getId(),
                user.getUsername(),
                friend.getId(),
                friend.getUsername(),
                entity.getStatus().toString()
        );
    }
}
