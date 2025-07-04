package com.shokoku.streamfix.controller.user;

import com.shokoku.streamfix.controller.user.request.UserLoginRequest;
import com.shokoku.streamfix.controller.user.request.UserRegisterRequest;
import com.shokoku.streamfix.security.StreamFixAuthUser;
import com.shokoku.streamfix.token.FetchTokenUseCase;
import com.shokoku.streamfix.token.UpdateTokenUseCase;
import com.shokoku.streamfix.user.FetchUserUseCase;
import com.shokoku.streamfix.user.RegisterUserUseCase;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import com.shokoku.streamfix.user.response.UserResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final FetchTokenUseCase fetchTokenUseCase;
  private final FetchUserUseCase fetchUserUseCase;
  private final UpdateTokenUseCase updateTokenUseCase;

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

  @PostMapping("/api/v1/user/login")
  public StreamFixApiResponse<String> login(@RequestBody UserLoginRequest request) {
    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(token);

    StreamFixAuthUser principal = (StreamFixAuthUser) authenticate.getPrincipal();

    return StreamFixApiResponse.ok("access-token");
  }

  @PostMapping("/api/v1/user/callback")
  public StreamFixApiResponse<String> kakaoCallback(@RequestBody Map<String, String> request) {
    String code = request.get("code");
    String accessTokenFromKakao = fetchTokenUseCase.getTokenFromKakao(code);
    UserResponse kakaoUser = fetchUserUseCase.findKakaoUser(accessTokenFromKakao);

    UserResponse byProviderId = fetchUserUseCase.findByProviderId(kakaoUser.providerId());

    if (ObjectUtils.isEmpty(byProviderId)) {
      registerUserUseCase.registerSocialUser(
          kakaoUser.username(), kakaoUser.provider(), kakaoUser.providerId());
    }

    return StreamFixApiResponse.ok(updateTokenUseCase.upsertToken(kakaoUser.providerId()));
  }
}
