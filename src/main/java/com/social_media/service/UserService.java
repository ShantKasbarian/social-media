package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PageDto<User, UserDto> searchByUsername(String username, Pageable pageable);
    void updateUsername(User user, String username);
    void updateEmail(User user, @Email String email);
    void updatePassword(User user, String password);
    FriendRequest blockUser(String targetId, User user);
    FriendRequest unblockUser(String id, User user);
    PageDto<User, UserDto> getBlockedUsers(User user, Pageable pageable);
}
