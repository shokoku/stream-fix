package com.shokoku.streamfix.movie;

import org.springframework.stereotype.Component;

@Component
public class SilverRoleValidator implements UserDownloadMovieRoleValidator {

  @Override
  public boolean validate(long count) {
    return count < 10;
  }

  @Override
  public boolean isTarget(String role) {
    return role.equals("ROLE_SILVER");
  }
}
