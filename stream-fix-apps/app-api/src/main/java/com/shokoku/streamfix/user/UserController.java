package com.shokoku.streamfix.user;

import com.shokoku.streamfix.controller.StreamFixApiResponse;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.request.UserRegisterRequest;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/api/v1/user/register")
  public StreamFixApiResponse<UserRegisterResponse> register(
      @RequestBody UserRegisterRequest request) {
    UserRegisterResponse register =
        registerUserUseCase.register(
            UserRegisterCommand.builder()
                .email(request.email())
                .username(request.username())
                .encryptedPassword(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .build());
    return StreamFixApiResponse.ok(register);
  }
}
