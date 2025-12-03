package com.social_media.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PasswordValidator {
    private static final int MIN_REQUIRED_LENGTH = 6;

    private static final String UPPERCASE_REGEX = "[A-Z]";

    private static final String LOWERCASE_REGEX = "[a-z]";

    private static final String NUMBER_REGEX = "[0-9]";

    private static final String SPECIAL_CHARACTERS_REGEX = ".*[!@#$%^&*(),.?\":{}|<>+].*";


    public static boolean isPasswordValid(String password) {
        log.info("validating password");

        if (password == null) {
            return false;
        }

        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);
        Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
        Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
        Pattern specialCharacterPattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);

        Matcher number = numberPattern.matcher(password);
        Matcher uppercase = uppercasePattern.matcher(password);
        Matcher lowercase = lowercasePattern.matcher(password);
        Matcher specialCharacter = specialCharacterPattern.matcher(password);

        boolean isValid = password.length() > MIN_REQUIRED_LENGTH && number.find() && uppercase.find() && lowercase.find() && specialCharacter.find();

        log.info("validated password");

        return isValid;
    }
}
