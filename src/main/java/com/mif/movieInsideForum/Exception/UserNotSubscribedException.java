package com.mif.movieInsideForum.Exception;

public class UserNotSubscribedException extends RuntimeException {
    public UserNotSubscribedException(String message) {
        super(message);
    }
}
