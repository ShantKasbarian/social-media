package com.social_media.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserDto(
        UUID id,
        @NotBlank(message = "email must be specified")
        @Email(message = "email is invalid")
        String email,
        @NotBlank(message = "username must be specified") String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "password must be specified") String password,
        @NotBlank(message = "firstname must be specified") String firstname,
        @NotBlank(message = "lastname must be specified") String lastname
) {
}
