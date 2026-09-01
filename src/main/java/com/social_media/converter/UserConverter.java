package com.social_media.converter;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter
    implements ToEntityConverter<User, UserDto>, ToModelConverter<User, UserDto> {
  @Override
  public User convertToEntity(UserDto model) {
    return new User(
        model.email(), model.password(), model.username(), model.firstname(), model.lastname());
  }

  @Override
  public UserDto convertToModel(User entity) {
    return new UserDto(
        entity.getId(),
        entity.getEmail(),
        entity.getUsername(),
        entity.getPassword(),
        entity.getFirstname(),
        entity.getLastname());
  }
}
