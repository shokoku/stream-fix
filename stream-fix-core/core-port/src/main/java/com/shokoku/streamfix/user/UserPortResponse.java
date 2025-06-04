package com.shokoku.streamfix.user;

import lombok.Builder;

@Builder
public record UserPortResponse(
    String userId,
    String username,
    String password,
    String email,
    String phone,
    String provider,
    String providerId,
    String role) {}
