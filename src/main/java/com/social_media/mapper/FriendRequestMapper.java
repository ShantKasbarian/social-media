package com.social_media.mapper;

import com.social_media.entity.FriendRequest;
import com.social_media.model.FriendRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FriendRequestMapper {
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "targetUser.id", target = "targetUserId")
  @Mapping(source = "targetUser.username", target = "targetUsername")
  FriendRequestDto toModel(FriendRequest entity);
}
