package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserBlockRepository;
import com.social_media.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class UserBlockServiceImplTest {
  private static final String USER_BLOCK_ALREADY_EXISTS_MESSAGE =
      "cannot block user because a block relationship already exists";

  private static final String USER_BLOCK_NOT_FOUND_MESSAGE = "user block not found";

  private static final String CANNOT_SELF_BLOCK_MESSAGE = "self block not allowed";

  private static final String CANNOT_DELETE_USER_BLOCK_MESSAGE =
      "cannot unblock user because you are not the blocker";

  @InjectMocks private UserBlockServiceImpl userBlockServiceImpl;

  @Mock private UserBlockRepository userBlockRepository;

  @Mock private UserRepository userRepository;

  @Mock private FriendRequestRepository friendRequestRepository;

  private User user;

  private User user2;

  private UserBlock userBlock;

  private Page<UserBlock> userBlocks;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    user2.setId(UUID.randomUUID());

    userBlock = new UserBlock(user, user2);
    userBlock.setId(UUID.randomUUID());

    userBlocks = new PageImpl<>(List.of(userBlock));
  }

  @Test
  void create() {
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(userRepository.getReferenceById(any(UUID.class))).thenReturn(user);
    when(userBlockRepository.save(any(UserBlock.class))).thenReturn(userBlock);
    doNothing()
        .when(friendRequestRepository)
        .deleteByUserIdTargetUserId(any(UUID.class), any(UUID.class));

    var response = userBlockServiceImpl.create(user, user2.getId());

    assertNotNull(response);
    verify(userBlockRepository).existsBlockBetween(any(UUID.class), any(UUID.class));
    verify(userBlockRepository).save(any(UserBlock.class));
    verify(friendRequestRepository).deleteByUserIdTargetUserId(any(UUID.class), any(UUID.class));
  }

  @Test
  void createShouldThrowRequestNotAllowedExceptionWhenCurrentUserIdEqualsTargetUserId() {
    Exception exception =
        assertThrows(
            RequestNotAllowedException.class,
            () -> userBlockServiceImpl.create(user, user.getId()));
    assertEquals(CANNOT_SELF_BLOCK_MESSAGE, exception.getMessage());
  }

  @Test
  void createShouldThrowResourceAlreadyExistsExceptionWhenUserBlockAlreadyExists() {
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class))).thenReturn(true);

    Exception exception =
        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> userBlockServiceImpl.create(user, user2.getId()));
    assertEquals(USER_BLOCK_ALREADY_EXISTS_MESSAGE, exception.getMessage());
  }

  @Test
  void delete() {
    when(userBlockRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(userBlock));
    doNothing().when(userBlockRepository).delete(any(UserBlock.class));

    userBlockServiceImpl.delete(user, userBlock.getId());

    verify(userBlockRepository).findById(any(UUID.class));
    verify(userBlockRepository).delete(any(UserBlock.class));
  }

  @Test
  void deleteShouldThrowResourceNotFoundExceptionWhenUserBlockDoesNotExist() {
    when(userBlockRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> userBlockServiceImpl.delete(user, userBlock.getId()));
    assertEquals(USER_BLOCK_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void deleteShouldThrowRequestNotAllowedExceptionWhenUserIsNotBlocker() {
    when(userBlockRepository.findById(any(UUID.class))).thenReturn(Optional.of(userBlock));

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class,
            () -> userBlockServiceImpl.delete(user2, userBlock.getId()));
    assertEquals(CANNOT_DELETE_USER_BLOCK_MESSAGE, exception.getMessage());
  }

  @Test
  void findByUserId() {
    when(userBlockRepository.findByUserId(any(UUID.class), any(Pageable.class)))
        .thenReturn(userBlocks);

    var response = userBlockServiceImpl.findByUserId(user.getId(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertEquals(userBlocks, response);
    verify(userBlockRepository).findByUserId(any(UUID.class), any(Pageable.class));
  }
}
