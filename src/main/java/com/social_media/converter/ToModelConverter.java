package com.social_media.converter;

public interface ToModelConverter<E, M> {
    M convertToModel(E entity);
}
