package com.shokoku.streamfix.repository.token;

import static com.shokoku.streamfix.entity.token.QTokenEntity.*;

import com.querydsl.jpa.JPQLQueryFactory;
import com.shokoku.streamfix.entity.token.TokenEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenCustomRepositoryImpl implements TokenCustomRepository {

  private final JPQLQueryFactory jpqlQueryFactory;

  @Override
  public Optional<TokenEntity> findByUserId(String userId) {
    return jpqlQueryFactory
        .selectFrom(tokenEntity)
        .where(tokenEntity.userId.eq(userId))
        .fetch()
        .stream()
        .findFirst();
  }
}
