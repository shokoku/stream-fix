package com.shokoku.streamfix.controller.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginRequest {

  private final String email;
  private String password;
}
