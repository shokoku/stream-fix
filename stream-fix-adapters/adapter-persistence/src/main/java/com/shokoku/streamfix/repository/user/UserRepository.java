package com.shokoku.streamfix.repository.user;

import com.shokoku.streamfix.entity.user.UserEntity;
import com.shokoku.streamfix.user.FetchUserPort;
import com.shokoku.streamfix.user.UserPortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements FetchUserPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public Optional<UserPortResponse> findByEmail(String email) {
    Optional<UserEntity> byEmail = userJpaRepository.findByEmail(email);

    return byEmail.map(
        userEntity ->
            UserPortResponse.builder()
                .userId(userEntity.getUserID())
                .password(userEntity.getPassword())
                .username(userEntity.getEmail())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build());
  }
}
