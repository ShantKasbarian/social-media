package com.social_media.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record UserDto(
        String id,
        @Email
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String username,
        String name,
        String lastname
) {
}
