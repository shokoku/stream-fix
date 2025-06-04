package com.shokoku.streamfix.token;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record StreamFixToken(
    String accessToken,
    String refreshToken,
    LocalDateTime accessTokenExpireAt,
    LocalDateTime refreshTokenExpireAt) {}
