package com.shokoku.streamfix.repository.token;

import com.shokoku.streamfix.entity.token.TokenEntity;
import java.util.Optional;

public interface TokenCustomRepository {
  Optional<TokenEntity> findByUserId(String userId);
}
