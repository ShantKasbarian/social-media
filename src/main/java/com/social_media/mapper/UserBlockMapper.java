package com.social_media.mapper;

import com.social_media.entity.UserBlock;
import com.social_media.model.UserBlockDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserBlockMapper {
  @Mapping(source = "user.id", target = "blockerId")
  @Mapping(source = "user.username", target = "blockerUsername")
  @Mapping(source = "targetUser.id", target = "targetUserId")
  @Mapping(source = "targetUser.username", target = "targetUsername")
  UserBlockDto toModel(UserBlock entity);
}
