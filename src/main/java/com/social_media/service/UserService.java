package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  void updateUser(User user, UserDto userDto);

  Page<User> searchByUsername(String username, Pageable pageable);
}
