package com.shokoku.streamfix.movie;

import org.springframework.stereotype.Component;

@Component
public class BronzeRoleValidator implements UserDownloadMovieRoleValidator {

  @Override
  public boolean validate(long count) {
    return count < 5;
  }

  @Override
  public boolean isTarget(String role) {
    return role.equals("ROLE_BRONZE");
  }
}
