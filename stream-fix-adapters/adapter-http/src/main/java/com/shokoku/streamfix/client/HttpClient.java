package com.shokoku.streamfix.client;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpClient {

  private final RestTemplate restTemplate;

  public String request(String uri, HttpMethod method, HttpHeaders headers, Map<String, Object> params) {
    return restTemplate.exchange(
        uri,
        method,
        new HttpEntity<>(headers),
        new ParameterizedTypeReference<String>() {},
        params
    ).getBody();
  }

}
