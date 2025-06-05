package com.shokoku.streamfix.user;

import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;

public interface RegisterUserUseCase {

  UserRegisterResponse register(UserRegisterCommand command);

  UserRegisterResponse registerSocialUser(String username, String provider, String providerId);
}
