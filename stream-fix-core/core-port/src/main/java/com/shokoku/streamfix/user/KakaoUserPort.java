package com.shokoku.streamfix.user;

public interface KakaoUserPort {

  UserPortResponse findUserFromKakao(String accessToken);
}
