package com.social_media.converters;

public interface ToModelConverter<E, M> {
    M convertToModel(E entity);
}
