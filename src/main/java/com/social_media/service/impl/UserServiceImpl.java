package com.social_media.service.impl;

import com.social_media.converter.UserConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.FriendshipStatus;
import com.social_media.entity.User;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.social_media.utils.PasswordValidator.INVALID_PASSWORD_MESSAGE;
import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
public class UserServiceImpl implements UserService {
    private static final String USERNAME_CANNOT_CONTAIN_SPACES_MESSAGE = "username cannot contain spaces";

    private static final String USERNAME_ALREADY_TAKEN_MESSAGE = "username is already taken";

    private static final String EMAIL_ALREADY_TAKEN_MESSAGE = "email is already taken";

    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String FRIEND_REQUEST_NOT_FOUND_MESSAGE = "friend request not found";

    private static final String BLOCKED_USER_MESSAGE = "you have been blocked by this user";

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserConverter userConverter;

    private final FriendRequestRepository friendRequestRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            @Lazy BCryptPasswordEncoder bCryptPasswordEncoder,
            UserConverter userConverter,
            FriendRequestRepository friendRequestRepository
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userConverter = userConverter;
        this.friendRequestRepository = friendRequestRepository;
    }

    @Override
    public PageDto<User, UserDto> searchByUsername(String username, Pageable pageable) {
        return new PageDto<>(userRepository.findByUsernameContainingIgnoreCase(username, pageable), userConverter);
    }

    @Override
    @Transactional
    public void updateUsername(User user, String username) {
        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        username = username.trim();
        if (username.contains(" ")) {
            throw new InvalidProvidedInfoException(USERNAME_CANNOT_CONTAIN_SPACES_MESSAGE);
        }

        if (userRepository.existsByUsername(username) && !user.getUsername().equals(username)) {
            throw new ResourceAlreadyExistsException(USERNAME_ALREADY_TAKEN_MESSAGE);
        }

        user.setUsername(username);

        userRepository.save(user);
    }

    @Override
    public void updateEmail(User user, @Email String email) {
        if (email == null|| email.isEmpty()) {
            throw new InvalidProvidedInfoException("email cannot be empty");
        }

        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new ResourceAlreadyExistsException(EMAIL_ALREADY_TAKEN_MESSAGE);
        }

        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(User user, String password) {
        if (password == null || password.isEmpty() || !isPasswordValid(password)) {
            throw new InvalidProvidedInfoException(INVALID_PASSWORD_MESSAGE);
        }

        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public FriendRequest blockUser(String targetId, User user) {
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        FriendRequest friendRequest = friendRequestRepository.findByUserIdFriendId(user.getId(), targetId)
                .orElse(new FriendRequest(user, targetUser));

        friendRequest.setStatus(FriendshipStatus.BLOCKED);
        friendRequestRepository.save(friendRequest);

        return friendRequest;
    }

    @Override
    @Transactional
    public FriendRequest unblockUser(String id, User user) {
        FriendRequest friendRequest = friendRequestRepository.findByUserIdFriendId(user.getId(), id)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        if (friendRequest.getUser().getId().equals(id)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        friendRequest.setStatus(FriendshipStatus.PENDING);
        friendRequestRepository.save(friendRequest);

        return friendRequest;
    }

    @Override
    public PageDto<User, UserDto> getBlockedUsers(User user, Pageable pageable) {
        List<User> blockedUsers = user.getBlockedUsers();

        return new PageDto<>(
                new PageImpl<>(blockedUsers, pageable, blockedUsers.size()),
                userConverter
        );
    }
}
