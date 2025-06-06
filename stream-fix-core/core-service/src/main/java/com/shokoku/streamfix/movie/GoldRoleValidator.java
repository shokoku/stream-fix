package com.shokoku.streamfix.movie;

import org.springframework.stereotype.Component;

@Component
public class GoldRoleValidator implements UserDownloadMovieRoleValidator {

  @Override
  public boolean validate(long count) {
    return true;
  }

  @Override
  public boolean isTarget(String role) {
    return role.equals("ROLE_GOLD");
  }
}
