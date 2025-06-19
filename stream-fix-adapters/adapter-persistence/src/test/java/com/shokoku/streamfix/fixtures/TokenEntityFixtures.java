package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.entity.token.TokenEntity;
import com.shokoku.streamfix.token.TokenPortResponse;
import java.time.LocalDateTime;

/**
 * 토큰 엔티티 관련 테스트 픽스처 클래스
 *
 * <p>TokenEntity와 TokenPortResponse의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다. Repository 계층 테스트에서 사용되는 엔티티
 * 객체들을 관리합니다.
 */
public class TokenEntityFixtures {

  // 기본 테스트 데이터 상수
  public static final String DEFAULT_USER_ID = "testUser123";
  public static final String CUSTOM_USER_ID = "customUser456";
  public static final String DEFAULT_ACCESS_TOKEN = "test.access.token";
  public static final String DEFAULT_REFRESH_TOKEN = "test.refresh.token";
  public static final String NEW_ACCESS_TOKEN = "new.access.token";
  public static final String NEW_REFRESH_TOKEN = "new.refresh.token";
  public static final String UPDATED_ACCESS_TOKEN = "updated.access.token";
  public static final String UPDATED_REFRESH_TOKEN = "updated.refresh.token";

  /** 기본 TokenEntity 생성 */
  public static TokenEntity aTokenEntity() {
    return TokenEntity.newTokenEntity(DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 사용자 ID를 가진 TokenEntity 생성 */
  public static TokenEntity aTokenEntityWithUserId(String userId) {
    return TokenEntity.newTokenEntity(userId, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 토큰들을 가진 TokenEntity 생성 */
  public static TokenEntity aTokenEntityWith(
      String userId, String accessToken, String refreshToken) {
    return TokenEntity.newTokenEntity(userId, accessToken, refreshToken);
  }

  /** 새로운 토큰으로 TokenEntity 생성 */
  public static TokenEntity aNewTokenEntity() {
    return TokenEntity.newTokenEntity(DEFAULT_USER_ID, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
  }

  /** 업데이트된 토큰으로 TokenEntity 생성 */
  public static TokenEntity anUpdatedTokenEntity() {
    return TokenEntity.newTokenEntity(DEFAULT_USER_ID, UPDATED_ACCESS_TOKEN, UPDATED_REFRESH_TOKEN);
  }

  /** 특정 만료 시간을 가진 TokenEntity 생성 */
  public static TokenEntity aTokenEntityWithExpiry(
      String userId,
      String accessToken,
      String refreshToken,
      LocalDateTime accessExpiry,
      LocalDateTime refreshExpiry) {
    return new TokenEntity(userId, accessToken, refreshToken, accessExpiry, refreshExpiry);
  }

  /** 만료된 TokenEntity 생성 */
  public static TokenEntity anExpiredTokenEntity() {
    LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
    return aTokenEntityWithExpiry(
        DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN, pastTime, pastTime);
  }

  /** 기본 TokenPortResponse 생성 */
  public static TokenPortResponse aTokenPortResponse() {
    return new TokenPortResponse(DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 액세스 토큰을 가진 TokenPortResponse 생성 */
  public static TokenPortResponse aTokenPortResponseWithAccessToken(String accessToken) {
    return new TokenPortResponse(accessToken, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 리프레시 토큰을 가진 TokenPortResponse 생성 */
  public static TokenPortResponse aTokenPortResponseWithRefreshToken(String refreshToken) {
    return new TokenPortResponse(DEFAULT_ACCESS_TOKEN, refreshToken);
  }

  /** 커스텀 토큰들을 가진 TokenPortResponse 생성 */
  public static TokenPortResponse aTokenPortResponseWith(String accessToken, String refreshToken) {
    return new TokenPortResponse(accessToken, refreshToken);
  }

  /** 새로운 토큰으로 TokenPortResponse 생성 */
  public static TokenPortResponse aNewTokenPortResponse() {
    return new TokenPortResponse(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
  }

  /** 업데이트된 토큰으로 TokenPortResponse 생성 */
  public static TokenPortResponse anUpdatedTokenPortResponse() {
    return new TokenPortResponse(UPDATED_ACCESS_TOKEN, UPDATED_REFRESH_TOKEN);
  }

  /** null 사용자 ID 반환 */
  public static String aNullUserId() {
    return null;
  }

  /** 빈 사용자 ID 반환 */
  public static String anEmptyUserId() {
    return "";
  }

  /** 존재하지 않는 사용자 ID 반환 */
  public static String aNonExistentUserId() {
    return "nonExistentUser123";
  }

  /** null 액세스 토큰 반환 */
  public static String aNullAccessToken() {
    return null;
  }

  /** 빈 액세스 토큰 반환 */
  public static String anEmptyAccessToken() {
    return "";
  }

  /** null 리프레시 토큰 반환 */
  public static String aNullRefreshToken() {
    return null;
  }

  /** 빈 리프레시 토큰 반환 */
  public static String anEmptyRefreshToken() {
    return "";
  }
}
