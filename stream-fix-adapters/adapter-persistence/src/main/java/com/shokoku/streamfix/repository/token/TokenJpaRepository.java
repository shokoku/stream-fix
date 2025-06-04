package com.shokoku.streamfix.repository.token;

import com.shokoku.streamfix.entity.token.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository
    extends JpaRepository<TokenEntity, String>, TokenCustomRepository {}
