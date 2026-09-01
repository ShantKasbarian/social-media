package com.social_media.converter;

import com.social_media.entity.BaseEntity;

public interface ToEntityConverter<E extends BaseEntity, M> {
  E convertToEntity(M model);
}
