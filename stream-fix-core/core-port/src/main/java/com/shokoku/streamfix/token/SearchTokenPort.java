package com.shokoku.streamfix.token;

public interface SearchTokenPort {

  TokenPortResponse findByUserId(String userId);
}
