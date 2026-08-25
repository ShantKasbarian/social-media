package com.social_media.service;

import com.social_media.entity.User;

public interface Updatable<T, D> {
  T update(User user, D dto);
}
