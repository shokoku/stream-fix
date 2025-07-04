package com.shokoku.streamfix.filter;

import com.shokoku.streamfix.token.FetchTokenUseCase;
import com.shokoku.streamfix.user.response.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final FetchTokenUseCase fetchTokenUseCase;

  public Authentication getAuthentication(String accessToken) {
    UserResponse user = fetchTokenUseCase.findUserByAccessToken(accessToken);
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.role()));
    UserDetails principal =
        new User(
            user.username(),
            StringUtils.isBlank(user.password()) ? "password" : user.password(),
            authorities);
    return new UsernamePasswordAuthenticationToken(principal, user.userId(), authorities);
  }

  public String getUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (String) authentication.getCredentials();
  }

  public String getRole() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getAuthorities().stream().findAny().orElseThrow().getAuthority();
  }
}
