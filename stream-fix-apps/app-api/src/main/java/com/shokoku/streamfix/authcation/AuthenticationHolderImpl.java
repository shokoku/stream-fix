package com.shokoku.streamfix.authcation;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationHolderImpl implements AuthenticationHolder, RequestByProvider {

  private Authentication authentication;

  @Override
  public Optional<Authentication> getAuthentication() {
    return Optional.of(authentication);
  }

  @Override
  public void setAuthentication(Authentication authentication) {
    this.authentication = authentication;
  }

  @Override
  public Optional<String> getRequestedBy() {
    return getAuthentication().map(Authentication::getRequestBy);
  }
}
