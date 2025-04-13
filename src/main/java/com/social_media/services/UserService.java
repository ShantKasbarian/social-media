package com.social_media.services;

import com.social_media.converters.UserConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.PageDto;
import com.social_media.models.UserDto;
import com.social_media.repositories.FriendRequestRepository;
import com.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserConverter userConverter;

    private final FriendRequestRepository friendRequestRepository;

    public UserService(
            UserRepository userRepository,
            @Lazy
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserConverter userConverter,
            FriendRequestRepository friendRequestRepository
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userConverter = userConverter;
        this.friendRequestRepository = friendRequestRepository;
    }

    public PageDto<User, UserDto> searchByUsername(String username, Pageable pageable) {
        return new PageDto<>(userRepository.findByUsernameContainingIgnoreCase(username, pageable), userConverter);
    }

    @Transactional
    public User updateUsername(User user, String username) {
        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        username = username.trim();
        if (username.contains(" ")) {
            throw new InvalidProvidedInfoException("username cannot contain spaces");
        }

        if (userRepository.existsByUsername(username) && !user.getUsername().equals(username)) {
            throw new ResourceAlreadyExistsException("username is already taken, try another one");
        }

        user.setUsername(username);

        return userRepository.save(user);
    }

    public User updateEmail(User user, @Email String email) {
        if (email == null|| email.isEmpty()) {
            throw new InvalidProvidedInfoException("email cannot be empty");
        }

        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new ResourceAlreadyExistsException("email already in use");
        }

        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public String updatePassword(
            User user,
            String password
    ) {
        if (password == null || password.isEmpty() || !LoginSignupService.isPasswordValid(password)) {
            throw new InvalidProvidedInfoException("invalid password");
        }

        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return "password has been changed";
    }

    @Transactional
    public String blockUser(String targetId, User user) {
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        List<User> blockedUsers = user.getBlockedUsers();

        for (User blockedUser: blockedUsers) {
            if (blockedUser.getId().equals(targetId)) {
                throw new ResourceAlreadyExistsException("cannot block user more than once");
            }
        }

        user.getBlockedUsers().add(targetUser);
        userRepository.save(user);

        FriendRequest friendRequest = friendRequestRepository.findByUser_idFriend_id(user.getId(), targetId)
                .orElse(null);

        if (friendRequest != null) {
            friendRequest.setStatus(FriendshipStatus.BLOCKED);
            friendRequestRepository.save(friendRequest);
        }

        return "user has been blocked";
    }

    @Transactional
    public String unblockUser(String id, User user) {
        String message = null;

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        List<User> blockedUsers = user.getBlockedUsers();

        for(User blockedUser: blockedUsers) {
            if (blockedUser.getId().equals(id)) {
                user.getBlockedUsers().remove(blockedUser);
                userRepository.save(user);
                break;
            }
        }

        List<User> targetUserBlockedUsers = targetUser.getBlockedUsers();
        String currentUserId = user.getId();

        for (User blockedUser: targetUserBlockedUsers) {
            if (blockedUser.getId().equals(currentUserId)) {
                message = "waiting for other user to unblock you";
            }
        }

        FriendRequest friendRequest = friendRequestRepository.findByUser_idFriend_id(currentUserId, id)
                .orElse(null);

        if (friendRequest != null && message == null) {
            friendRequest.setStatus(FriendshipStatus.PENDING);
            friendRequestRepository.save(friendRequest);
        }

        if (message == null) {
            message = "user has been unblocked";
        }

        return message;
    }

    public PageDto<User, UserDto> getBlockedUsers(User user, Pageable pageable) {
        List<User> blockedUsers = user.getBlockedUsers();

        return new PageDto<>(
                new PageImpl<>(blockedUsers, pageable, blockedUsers.size()),
                userConverter
        );
    }
}
