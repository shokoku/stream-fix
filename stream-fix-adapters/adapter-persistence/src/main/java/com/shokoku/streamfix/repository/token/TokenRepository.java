package com.shokoku.streamfix.repository.token;

import com.shokoku.streamfix.token.InsertTokenPort;
import com.shokoku.streamfix.token.SearchTokenPort;
import com.shokoku.streamfix.token.TokenPortResponse;
import com.shokoku.streamfix.token.UpdateTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TokenRepository implements SearchTokenPort, InsertTokenPort, UpdateTokenPort {

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  @Transactional
  public TokenPortResponse create(String userId, String accessToken, String refreshToken) {
    return null;
  }

  @Override
  @Transactional
  public TokenPortResponse findByUserId(String userId) {
    return null;
  }

  @Override
  @Transactional
  public void updateToken(String userId, String accessToken, String refreshToken) {}
}
