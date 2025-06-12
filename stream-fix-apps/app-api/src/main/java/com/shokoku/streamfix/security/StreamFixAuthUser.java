package com.shokoku.streamfix.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class StreamFixAuthUser extends User {

  private final String userId;
  private final String username;
  private final String password;
  private final String email;
  private final String phone;

  public StreamFixAuthUser(
      String userId,
      String username,
      String password,
      String email,
      String phone,
      Collection<? extends GrantedAuthority> authorities) {
    super(email, password, authorities);
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.email = email;
    this.phone = phone;
  }
}
