package com.shokoku.streamfix.token;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record StreamFixToken(
    String accessToken,
    String refreshToken,
    LocalDateTime accessTokenExpireAt,
    LocalDateTime refreshTokenExpireAt) {}
