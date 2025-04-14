package com.social_media.converters;

public interface ToEntityConverter<E, M> {
    E convertToEntity(M model);
}
