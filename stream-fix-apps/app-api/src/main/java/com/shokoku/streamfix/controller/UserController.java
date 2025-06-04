package com.shokoku.streamfix.controller;

import com.shokoku.streamfix.user.RegisterUserUseCase;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.request.UserRegisterRequest;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;

  @PostMapping("/api/v1/user/register")
  public StreamFixApiResponse<UserRegisterResponse> register(
      @RequestBody UserRegisterRequest request) {
    UserRegisterResponse register =
        registerUserUseCase.register(
            UserRegisterCommand.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .encryptedPassword(request.getPassword())
                .phone(request.getPhone())
                .build());
    return StreamFixApiResponse.ok(register);
  }
}
