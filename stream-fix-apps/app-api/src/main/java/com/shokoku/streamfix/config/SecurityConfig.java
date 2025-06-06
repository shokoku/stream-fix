package com.shokoku.streamfix.config;

import com.shokoku.streamfix.filter.JwtAuthenticationFilter;
import com.shokoku.streamfix.security.StreamFixUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final StreamFixUserDetailsService streamFixUserDetailsService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.formLogin(AbstractHttpConfigurer::disable);
    httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    httpSecurity.userDetailsService(streamFixUserDetailsService);
    httpSecurity.authorizeHttpRequests(
        auth ->
            auth.requestMatchers(
                    "/api/v1/user/register", "/api/v1/user/login", "/api/v1/user/callback")
                .permitAll()
                .anyRequest()
                .authenticated());

    httpSecurity.oauth2Login(oauth2 -> oauth2.failureUrl("/login?error=true"));
    httpSecurity.addFilterBefore(
        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public CorsConfigurationSource corsConfigurationSource() {
    return request -> {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedHeaders(Collections.singletonList("*"));
      configuration.setAllowedMethods(Collections.singletonList("*"));
      configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
      configuration.setAllowCredentials(true);
      return configuration;
    };
  }
}
