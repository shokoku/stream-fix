package com.shokoku.streamfix.user;

public interface SocialUserPort {
  UserPortResponse create(CreateUser user);

  UserPortResponse createSocialUser(String username, String provider, String providerId);
}
