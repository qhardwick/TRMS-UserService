package com.skillstorm.exceptions;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, String username) {
        super(message + " " + username);
    }
}
