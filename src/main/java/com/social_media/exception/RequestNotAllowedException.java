package com.social_media.exception;

public class RequestNotAllowedException extends RuntimeException {
    public RequestNotAllowedException(String message) {
        super(message);
    }
}
