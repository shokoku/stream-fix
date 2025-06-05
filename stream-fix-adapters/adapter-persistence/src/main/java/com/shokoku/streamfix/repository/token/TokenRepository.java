package com.shokoku.streamfix.repository.token;

import com.shokoku.streamfix.entity.token.TokenEntity;
import com.shokoku.streamfix.token.InsertTokenPort;
import com.shokoku.streamfix.token.SearchTokenPort;
import com.shokoku.streamfix.token.TokenPortResponse;
import com.shokoku.streamfix.token.UpdateTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepository implements SearchTokenPort, InsertTokenPort, UpdateTokenPort {

  private final TokenJpaRepository tokenJpaRepository;

  @Override
  @Transactional
  public TokenPortResponse create(String userId, String accessToken, String refreshToken) {
    TokenEntity tokenEntity = TokenEntity.newTokenEntity(userId, accessToken, refreshToken);
    tokenJpaRepository.save(tokenEntity);
    return new TokenPortResponse(accessToken, refreshToken);
  }

  @Override
  @Transactional
  public TokenPortResponse findByUserId(String userId) {
    return tokenJpaRepository
        .findByUserId(userId)
        .map(result -> new TokenPortResponse(result.getAccessToken(), result.getRefreshToken()))
        .orElse(null);
  }

  @Override
  @Transactional
  public void updateToken(String userId, String accessToken, String refreshToken) {
    Optional<TokenEntity> byUserId = tokenJpaRepository.findByUserId(userId);
    if (byUserId.isEmpty()) {
      throw new RuntimeException();
    }

    TokenEntity tokenEntity = byUserId.get();
    tokenEntity.updateToken(accessToken, refreshToken);
    tokenJpaRepository.save(tokenEntity);
  }
}
