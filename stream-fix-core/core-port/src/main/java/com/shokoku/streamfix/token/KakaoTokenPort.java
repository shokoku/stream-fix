package com.shokoku.streamfix.token;

public interface KakaoTokenPort {

  String getAccessTokenByCode(String code);
}
