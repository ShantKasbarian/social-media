package com.social_media.converters;

import com.social_media.entities.User;
import com.social_media.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements
        ToEntityConverter<User, UserDto>,
        ToModelConverter<User, UserDto> {

    @Override
    public User convertToEntity(UserDto model) {
        User user = new User();
        user.setId(model.id());
        user.setEmail(model.email());
        user.setPassword(model.password());
        user.setUsername(model.username());
        user.setName(model.name());
        user.setLastname(model.lastname());
        return user;
    }

    @Override
    public UserDto convertToModel(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getUsername(),
                entity.getName(),
                entity.getLastname()
        );
    }
}
