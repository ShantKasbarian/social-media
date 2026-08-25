package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;

public interface AuthenticationService {
  TokenDto login(LoginDto loginDto);

  TokenDto signup(User user);
}
