package com.social_media.service;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBlockService {
  UserBlock createUserBlock(User currentUser, UUID targetUserId);

  Page<UserBlock> getUserBlocksByUserId(UUID userId, Pageable pageable);
}
