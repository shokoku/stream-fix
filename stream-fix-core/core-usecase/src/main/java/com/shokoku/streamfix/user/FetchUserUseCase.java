package com.shokoku.streamfix.user;

import com.shokoku.streamfix.user.command.UserResponse;

public interface FetchUserUseCase {

  UserResponse fetchUserByEmail(String email);
}
