package com.shokoku.streamfix.authcation;

import java.util.Optional;

public interface AuthenticationHolder {

  Optional<Authentication> getAuthentication();

  void setAuthentication(Authentication authentication);

}
