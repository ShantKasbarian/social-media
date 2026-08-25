package com.social_media.converter;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.model.UserBlockDto;
import org.springframework.stereotype.Component;

@Component
public class UserBlockConverter implements ToModelConverter<UserBlock, UserBlockDto> {
  @Override
  public UserBlockDto convertToModel(UserBlock entity) {
    User user = entity.getUser();
    User targetUser = entity.getTargetUser();

    return new UserBlockDto(
        entity.getId(),
        user.getId(),
        user.getUsername(),
        targetUser.getId(),
        targetUser.getUsername());
  }
}
