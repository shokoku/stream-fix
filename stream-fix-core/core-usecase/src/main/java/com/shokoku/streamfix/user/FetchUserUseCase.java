package com.shokoku.streamfix.user;

import com.shokoku.streamfix.user.response.UserResponse;

public interface FetchUserUseCase {

  UserResponse fetchUserByEmail(String email);

  UserResponse findByProviderId(String providerId);

  UserResponse findKakaoUser(String accessToken);
}
