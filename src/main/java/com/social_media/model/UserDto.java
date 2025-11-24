package com.social_media.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

import java.util.UUID;

public record UserDto(
        UUID id,
        @Email
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String username,
        String name,
        String lastname
) {
}
