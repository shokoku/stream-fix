package com.shokoku.streamfix.user;

import com.shokoku.streamfix.exception.UserException;
import com.shokoku.streamfix.exception.UserException.UserDoesNotExistException;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import com.shokoku.streamfix.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements FetchUserUseCase, RegisterUserUseCase {

  private final FetchUserPort fetchUserPort;
  private final InsertUserPort insertUserPort;
  private final KakaoUserPort kakaoUserPort;

  @Override
  public UserResponse fetchUserByEmail(String email) {
    Optional<UserPortResponse> byEmail = fetchUserPort.findByEmail(email);
    if (byEmail.isEmpty()) {
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

  @Override
  public UserResponse findByProviderId(String providerId) {
    return fetchUserPort
        .findByProviderId(providerId)
        .map(
            it ->
                UserResponse.builder()
                    .userId(it.userId())
                    .providerId(it.providerId())
                    .provider(it.provider())
                    .username(it.username())
                    .role(it.role())
                    .build())
        .orElse(null);
  }

  @Override
  public UserResponse findKakaoUser(String accessToken) {
    UserPortResponse userFromKakao = kakaoUserPort.findUserFromKakao(accessToken);
    return UserResponse.builder()
        .provider(userFromKakao.provider())
        .providerId(userFromKakao.providerId())
        .username(userFromKakao.username())
        .build();
  }

  @Override
  public UserRegisterResponse register(UserRegisterCommand command) {
    String email = command.email();
    Optional<UserPortResponse> byEmail = fetchUserPort.findByEmail(email);

    if (byEmail.isPresent()) {
      throw new UserException.UserAlreadyExistException();
    }

    UserPortResponse response =
        insertUserPort.create(
            CreateUser.builder()
                .username(command.username())
                .encryptedPassword(command.encryptedPassword())
                .email(command.email())
                .phone(command.phone())
                .build());

    return new UserRegisterResponse(response.username(), response.email(), response.phone());
  }

  @Override
  public UserRegisterResponse registerSocialUser(
      String username, String provider, String providerId) {
    Optional<UserPortResponse> byProviderId = fetchUserPort.findByProviderId(providerId);

    if (byProviderId.isPresent()) {
      return null;
    }
    UserPortResponse socialUser = insertUserPort.createSocialUser(username, provider, providerId);
    return new UserRegisterResponse(socialUser.username(), null, null);
  }
}
