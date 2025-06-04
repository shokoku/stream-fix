package com.shokoku.streamfix.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

  private final ErrorCode errorCode;

  public UserException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public static class UserDoesNotExistException extends UserException {
    public UserDoesNotExistException() {
      super(ErrorCode.USER_DOES_NOT_EXIST);
    }
  }
}
