package com.andriibryliant.movies.auth.exception;

public class BadRegisterRequestException extends RuntimeException {
    public BadRegisterRequestException(String message) {
        super(message);
    }
}
