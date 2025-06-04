package com.shokoku.streamfix.security;

import com.shokoku.streamfix.user.FetchUserUseCase;
import com.shokoku.streamfix.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamFixUserDetailsService implements UserDetailsService {

  private final FetchUserUseCase fetchUserUseCase;

  @Override
  public StreamFixAuthUser loadUserByUsername(String email) throws UsernameNotFoundException {
    UserResponse userResponse = fetchUserUseCase.fetchUserByEmail(email);
    return new StreamFixAuthUser(
        userResponse.userId(),
        userResponse.username(),
        userResponse.password(),
        userResponse.email(),
        userResponse.phone(),
        List.of(new SimpleGrantedAuthority(userResponse.role())));
  }
}
