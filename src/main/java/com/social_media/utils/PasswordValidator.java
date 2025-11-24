package com.social_media.utils;

import com.social_media.exception.InvalidCredentialsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
    public static final String INVALID_PASSWORD_MESSAGE = "password must be at least 6 characters long";

    private static final String UPPERCASE_REGEX = "[A-Z]";

    private static final String LOWERCASE_REGEX = "[a-z]";

    private static final String NUMBER_REGEX = "[0-9]";

    private static final String SPECIAL_CHARACTERS_REGEX = ".*[!@#$%^&*(),.?\":{}|<>+].*";


    public static boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            throw new InvalidCredentialsException(INVALID_PASSWORD_MESSAGE);
        }

        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);
        Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
        Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
        Pattern specialCharacterPattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);

        Matcher number = numberPattern.matcher(password);
        Matcher uppercase = uppercasePattern.matcher(password);
        Matcher lowercase = lowercasePattern.matcher(password);
        Matcher specialCharacter = specialCharacterPattern.matcher(password);

        return number.find() && uppercase.find() && lowercase.find() && specialCharacter.find();
    }
}
