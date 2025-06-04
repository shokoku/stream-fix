package com.shokoku.streamfix.token;

public interface KaKaoTokenPort {

  String getAccessTokenByCode(String code);
}
