package com.social_media.service;

import com.social_media.entity.UserBlock;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBlockService extends Creatable<UserBlock, UUID>, Deletable {
  Page<UserBlock> findByUserId(UUID userId, Pageable pageable);
}
