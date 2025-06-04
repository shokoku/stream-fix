package com.shokoku.streamfix.advice;

import com.shokoku.streamfix.controller.StreamFixApiResponse;
import com.shokoku.streamfix.exception.ErrorCode;
import com.shokoku.streamfix.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler(UserException.class)
  protected StreamFixApiResponse<?> handleUserException(UserException e) {
    log.error("error={}", e.getMessage(), e);
    return StreamFixApiResponse.fail(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected StreamFixApiResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("error={}", e.getMessage(), e);
    return StreamFixApiResponse.fail(ErrorCode.INVALID_PARAMETER, e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  protected StreamFixApiResponse<?> handleRuntimeException(RuntimeException e) {
    log.error("error={}", e.getMessage(), e);
    return StreamFixApiResponse.fail(ErrorCode.DEFAULT_ERROR, e.getMessage());
  }
}
