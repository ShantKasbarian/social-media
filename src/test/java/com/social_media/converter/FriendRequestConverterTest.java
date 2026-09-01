package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class FriendRequestConverterTest {
  @InjectMocks private FriendRequestConverter friendRequestConverter;

  private FriendRequest friendRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    User user1 = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");

    User user2 =
        new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");

    friendRequest = new FriendRequest(user1, user2, FriendRequest.Status.PENDING);
  }

  @Test
  void convertToModel() {
    FriendRequestDto friendRequestDto = friendRequestConverter.convertToModel(friendRequest);

    assertNotNull(friendRequestDto);
    assertEquals(friendRequest.getId(), friendRequestDto.id());
    assertEquals(friendRequest.getUser().getId(), friendRequestDto.userId());
    assertEquals(friendRequest.getUser().getUsername(), friendRequestDto.username());
    assertEquals(friendRequest.getTargetUser().getId(), friendRequestDto.targetUserId());
    assertEquals(friendRequest.getTargetUser().getUsername(), friendRequestDto.targetUsername());
    assertEquals(friendRequest.getStatus(), friendRequestDto.status());
  }
}
