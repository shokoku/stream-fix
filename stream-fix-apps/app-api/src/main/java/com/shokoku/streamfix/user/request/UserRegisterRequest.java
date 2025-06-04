package com.shokoku.streamfix.user.request;

public record UserRegisterRequest(String username, String password, String email, String phone) {}
