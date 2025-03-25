package com.social_media.exceptions;

public class RequestNotAllowedException extends RuntimeException {
    public RequestNotAllowedException(String message) {
        super(message);
    }
}
