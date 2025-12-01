package com.social_media.service;

import com.social_media.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {
    void updateUser(User user, User target);
    Page<User> searchByUsername(@NotNull(message = "username must be specified") String username, Pageable pageable);
}
