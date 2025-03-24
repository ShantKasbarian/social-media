package com.social_media.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        String id,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String username,
        String name,
        String lastname
) {
}
