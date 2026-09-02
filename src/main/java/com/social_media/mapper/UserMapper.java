package com.social_media.mapper;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
  User toEntity(UserDto model);

  UserDto toModel(User entity);
}
