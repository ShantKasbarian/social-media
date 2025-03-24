package com.social_media.converters;

public interface Converter<E, M> {
    E convertToEntity(M model);
    M convertToModel(E entity);
}
