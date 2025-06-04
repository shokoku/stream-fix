package com.shokoku.streamfix.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  DEFAULT_ERROR("SFX0000", "에러가 발생하였습니다."),
  USER_DOES_NOT_EXIST("SFX2000", "사용자가 존재하지 않습니다."),
  USER_ALREADY_EXIST("SFX2001", "사용자가 이미 존재합니다."),
  INVALID_PARAMETER("SFX4000", "입력 값이 잘못 되었습니다."),
  ;

  private final String code;
  private final String desc;

  @Override
  public String toString() {
    return "[" + code + "] " + desc;
  }
}
