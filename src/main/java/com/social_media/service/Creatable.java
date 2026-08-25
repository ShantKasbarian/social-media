package com.social_media.service;

import com.social_media.entity.User;

public interface Creatable<T, D> {
  T create(User user, D dto);
}
