package com.social_media.utils;

import org.springframework.validation.annotation.Validated;

@Validated
public interface UsernameValidator {
    boolean isUsernameValid(String username);
}
