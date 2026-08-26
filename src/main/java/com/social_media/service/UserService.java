package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.model.UserPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService extends Updatable<User, UserPatchDto> {
  Page<User> findByUsername(String username, Pageable pageable);
}
