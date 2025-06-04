package com.shokoku.streamfix.token;

import com.shokoku.streamfix.token.response.TokenResponse;

public interface CreateTokenUseCase {

  TokenResponse createNewToken(String userId);
}
