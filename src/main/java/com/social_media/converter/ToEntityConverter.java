package com.social_media.converter;

public interface ToEntityConverter<E, M> {
    E convertToEntity(M model);
}
