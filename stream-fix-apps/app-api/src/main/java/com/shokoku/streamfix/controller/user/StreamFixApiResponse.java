package com.shokoku.streamfix.controller.user;

import com.shokoku.streamfix.exception.ErrorCode;

public record StreamFixApiResponse<T>(boolean success, String code, String message, T data) {

  public static final String CODE_SUCCESS = "SUCCEED";

  public static <T> StreamFixApiResponse<T> ok(T data) {
    return new StreamFixApiResponse<>(true, CODE_SUCCESS, null, data);
  }

  public static <T> StreamFixApiResponse<T> fail(ErrorCode errorCode, String message) {
    return new StreamFixApiResponse<>(false, errorCode.getCode(), message, null);
  }
}
