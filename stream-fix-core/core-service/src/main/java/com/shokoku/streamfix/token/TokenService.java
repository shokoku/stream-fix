package com.shokoku.streamfix.token;

import com.shokoku.streamfix.token.response.TokenResponse;
import com.shokoku.streamfix.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService implements FetchTokenUseCase, CreateTokenUseCase, UpdateTokenUseCase {

  @Override
  public TokenResponse createNewToken(String userId) {
    return null;
  }

  @Override
  public Boolean validateToken(String accessToken) {
    return null;
  }

  @Override
  public String getTokenFromKakao(String code) {
    return "";
  }

  @Override
  public UserResponse findUserByAccessToken(String accessToken) {
    return null;
  }
}
