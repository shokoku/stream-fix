package com.shokoku.streamfix.token;

public interface InsertTokenPort {

  TokenPortResponse create(String userId, String accessToken, String refreshToken);
}
