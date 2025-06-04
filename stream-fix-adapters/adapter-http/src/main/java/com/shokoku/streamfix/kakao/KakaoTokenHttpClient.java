package com.shokoku.streamfix.kakao;

import com.shokoku.streamfix.token.KaKaoTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KakaoTokenHttpClient implements KaKaoTokenPort {

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String kakaoClientId;

  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  private String kakaoClientSecret;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";

  @Override
  public String getAccessTokenByCode(String code) {
    RestTemplate restTemplate = new RestTemplate();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoClientId);
    params.add("redirect_uri", kakaoRedirectUri);
    params.add("code", code);
    params.add("client_secret", kakaoClientSecret);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<Map> exchange =
        restTemplate.exchange(KAKAO_TOKEN_URL, HttpMethod.POST, request, Map.class);

    return Objects.requireNonNull(exchange.getBody()).get("access_token").toString();
  }
}
