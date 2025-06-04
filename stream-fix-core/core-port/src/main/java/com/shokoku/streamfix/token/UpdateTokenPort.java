package com.shokoku.streamfix.token;

public interface UpdateTokenPort {

  void updateToken(String userId, String accessToken, String refreshToken);
}
