package com.shokoku.streamfix.user.request;

import com.shokoku.streamfix.annotaion.PasswordEncryption;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterRequest {

  private final String username;

  @PasswordEncryption private String password;

  private final String email;

  private final String phone;
}
