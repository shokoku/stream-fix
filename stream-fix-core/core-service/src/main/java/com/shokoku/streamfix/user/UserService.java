package com.shokoku.streamfix.user;

import com.shokoku.streamfix.exception.UserException.UserDoesNotExistException;
import com.shokoku.streamfix.user.command.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements FetchUserUseCase {

  private final FetchUserPort fetchUserPort;

  @Override
  public UserResponse fetchUserByEmail(String email) {
    Optional<UserPortResponse> byEmail = fetchUserPort.findByEmail(email);
    if (byEmail.isPresent()) {
      throw new UserDoesNotExistException();
    }

    UserPortResponse userPortResponse = byEmail.get();
    return UserResponse.builder()
        .userId(userPortResponse.userId())
        .email(userPortResponse.email())
        .password(userPortResponse.password())
        .username(userPortResponse.username())
        .role(userPortResponse.role())
        .build();
  }
}
