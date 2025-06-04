package com.shokoku.streamfix.token.response;

import lombok.Builder;

@Builder
public record TokenResponse(String accessToken, String refreshToken) {}
