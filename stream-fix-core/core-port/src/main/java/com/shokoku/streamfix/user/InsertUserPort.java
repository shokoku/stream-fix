package com.shokoku.streamfix.user;

public interface InsertUserPort {
  UserPortResponse create(CreateUser user);

  UserPortResponse createSocialUser(String username, String provider, String providerId);
}
