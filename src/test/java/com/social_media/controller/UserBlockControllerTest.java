package com.social_media.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.converter.UserBlockConverter;
import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.model.UserBlockDto;
import com.social_media.service.UserBlockService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

class UserBlockControllerTest {
  @InjectMocks private UserBlockController userBlockController;

  @Mock private UserBlockService userBlockService;

  @Mock private UserBlockConverter userBlockConverter;

  @Mock private Authentication authentication;

  private User user2;

  private UserBlock userBlock;

  private UserBlockDto userBlockDto;

  private Page<UserBlock> userBlocks;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("john.doe");

    user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setUsername("jack.doe");

    userBlock = new UserBlock(UUID.randomUUID(), user, user2);
    userBlockDto =
        new UserBlockDto(
            userBlock.getId(),
            user.getId(),
            user.getUsername(),
            user2.getId(),
            user2.getUsername());
    userBlocks = new PageImpl<>(List.of(userBlock));

    when(authentication.getPrincipal()).thenReturn(user);
  }

  @Test
  void createUserBlock() {
    when(userBlockService.create(any(User.class), any(UUID.class))).thenReturn(userBlock);
    when(userBlockConverter.convertToModel(any(UserBlock.class))).thenReturn(userBlockDto);

    var response = userBlockController.createUserBlock(authentication, user2.getId());

    assertNotNull(response);
    assertEquals(userBlockDto, response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(userBlockService).create(any(User.class), any(UUID.class));
    verify(userBlockConverter).convertToModel(any(UserBlock.class));
  }

  @Test
  void getUserBlocksByUserId() {
    when(userBlockService.findByUserId(any(UUID.class), any(Pageable.class)))
        .thenReturn(userBlocks);
    when(userBlockConverter.convertToModel(any(UserBlock.class))).thenReturn(userBlockDto);

    var response = userBlockController.getUserBlocksByUserId(authentication, 0, 10);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(userBlocks.getTotalElements(), response.getBody().getTotalElements());
    assertEquals(userBlocks.getContent().size(), response.getBody().getContent().size());
    verify(userBlockService).findByUserId(any(UUID.class), any(Pageable.class));
    verify(userBlockConverter, atLeastOnce()).convertToModel(any(UserBlock.class));
  }

  @Test
  void deleteUserBlock() {
    doNothing().when(userBlockService).delete(any(User.class), any(UUID.class));

    var response = userBlockController.deleteUserBlock(authentication, userBlock.getId());

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(userBlockService).delete(any(User.class), any(UUID.class));
  }
}
