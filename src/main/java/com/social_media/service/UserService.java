package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PageDto<User, UserDto> searchByUsername(String username, Pageable pageable);
    User updateUsername(User user, String username);
    User updateEmail(User user, @Email String email);
    String updatePassword(User user, String password);
    String blockUser(String targetId, User user);
    String unblockUser(String id, User user);
    PageDto<User, UserDto> getBlockedUsers(User user, Pageable pageable);
}
