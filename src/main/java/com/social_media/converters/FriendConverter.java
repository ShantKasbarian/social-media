package com.social_media.converters;

import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.models.FriendDto;
import org.springframework.stereotype.Component;

@Component
public class FriendConverter implements Converter<Friend, FriendDto> {
    @Override
    public Friend convertToEntity(FriendDto model) {
        Friend friend = new Friend();
        friend.setId(model.id());
        friend.setStatus(FriendshipStatus.valueOf(model.status()));
        return friend;
    }

    @Override
    public FriendDto convertToModel(Friend entity) {
        User user = entity.getUser();
        User recipient = entity.getFriend();
        return new FriendDto(
                entity.getId(),
                user.getId(),
                user.getUsername(),
                recipient.getId(),
                recipient.getUsername(),
                entity.getStatus().toString()
        );
    }
}
