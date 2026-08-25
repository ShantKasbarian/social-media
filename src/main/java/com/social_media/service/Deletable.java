package com.social_media.service;

import com.social_media.entity.User;
import java.util.UUID;

public interface Deletable {
  void delete(User user, UUID id);
}
