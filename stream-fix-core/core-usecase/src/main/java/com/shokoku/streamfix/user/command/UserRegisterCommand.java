package com.shokoku.streamfix.user.command;

import lombok.Builder;

@Builder
public record UserRegisterCommand(
    String username, String encryptedPassword, String email, String phone) {}
