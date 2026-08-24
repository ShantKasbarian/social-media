package com.social_media.service;

import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  void updateUser(User user, User target);

  Page<User> searchByUsername(String username, Pageable pageable);
}
