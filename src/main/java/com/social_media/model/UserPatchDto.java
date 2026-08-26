package com.social_media.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPatchDto(
    @NotBlank(message = "email must be specified") @Email(message = "email is invalid")
        String email,
    @NotBlank(message = "username must be specified")
        @Size(min = 5, max = 20, message = "username must be between 5 to 20 characters long")
        @Pattern(regexp = "^\\S+$", message = "username must not contain spaces")
        String username,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank(message = "password must be specified")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}$",
            message =
                "password must be at least 6 characters long contain one uppercase, one lowercase, one digit and one special character")
        String password) {}
