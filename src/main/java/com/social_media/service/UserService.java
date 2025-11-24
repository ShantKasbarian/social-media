package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<User> searchByUsername(String username, Pageable pageable);
    void updateUsername(User user, String username);
    void updateEmail(User user, @Email String email);
    void updatePassword(User user, String password);
    FriendRequest blockUser(UUID targetId, User user);
    FriendRequest unblockUser(UUID id, User user);
    Page<User> getBlockedUsers(User user, Pageable pageable);
}
