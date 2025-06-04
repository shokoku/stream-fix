package com.shokoku.streamfix.token;

import com.shokoku.streamfix.user.response.UserResponse;

public interface FetchTokenUseCase {

  Boolean validateToken(String accessToken);

  String getTokenFromKakao(String code);

  UserResponse findUserByAccessToken(String accessToken);
}
