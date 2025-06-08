package com.mif.movieInsideForum.Exception;

public class UserAlreadySubscribedException extends RuntimeException {
  public UserAlreadySubscribedException(String message) {
    super(message);
  }
}