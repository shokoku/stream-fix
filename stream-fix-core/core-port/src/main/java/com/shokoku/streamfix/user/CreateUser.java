package com.shokoku.streamfix.user;

import lombok.Builder;

@Builder
public record CreateUser(String username, String encryptedPassword, String email, String phone) {}
