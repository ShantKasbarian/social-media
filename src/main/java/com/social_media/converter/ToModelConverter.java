package com.social_media.converter;

import com.social_media.entity.BaseEntity;

public interface ToModelConverter<E extends BaseEntity, M> {
  M convertToModel(E entity);
}
