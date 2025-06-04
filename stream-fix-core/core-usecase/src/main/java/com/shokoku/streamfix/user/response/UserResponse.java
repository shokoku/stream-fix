package com.shokoku.streamfix.user.response;

import lombok.Builder;

@Builder
public record UserResponse(
    String userId,
    String username,
    String password,
    String email,
    String phone,
    String provider,
    String providerId,
    String role) {}
