package com.shokoku.streamfix.user;

import lombok.Builder;

@Builder
public record StreamFixUser(
    String userId,
    String userName,
    String encryptedPassword,
    String email,
    String phone,
    String provider,
    String providerId,
    String role) {}
