package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.model.TokenDto;

public interface AuthenticationService {
    TokenDto login(String username, String password);
    String signup(User user);
}
