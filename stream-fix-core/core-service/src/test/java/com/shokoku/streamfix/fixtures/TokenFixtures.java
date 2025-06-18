package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.support.JwtTestHelper;
import com.shokoku.streamfix.token.TokenPortResponse;

/**
 * 토큰 관련 테스트 픽스처 클래스
 *
 * <p>JWT 토큰과 토큰 응답 객체의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다. JwtTestHelper와 연동하여 다양한 시나리오의 JWT 토큰을 생성할 수
 * 있습니다.
 */
public class TokenFixtures {

  // 기본 테스트 데이터 상수
  public static final String DEFAULT_ACCESS_TOKEN = "default.access.token";
  public static final String DEFAULT_REFRESH_TOKEN = "default.refresh.token";
  public static final String DEFAULT_USER_ID = "testUser123";
  public static final String DEFAULT_PROVIDER_ID = "testProviderId";
  public static final String DEFAULT_KAKAO_ACCESS_TOKEN = "kakaoAccessToken123";

  /** 기본 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponse() {
    return new TokenPortResponse(DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 액세스 토큰을 가진 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWithAccessToken(String accessToken) {
    return new TokenPortResponse(accessToken, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 리프레시 토큰을 가진 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWithRefreshToken(String refreshToken) {
    return new TokenPortResponse(DEFAULT_ACCESS_TOKEN, refreshToken);
  }

  /** 커스텀 토큰들을 가진 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWith(String accessToken, String refreshToken) {
    return new TokenPortResponse(accessToken, refreshToken);
  }

  /** 유효한 JWT 액세스 토큰을 가진 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWithValidJwt() {
    String validJwtToken = JwtTestHelper.generateValidToken(DEFAULT_USER_ID);
    return new TokenPortResponse(validJwtToken, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 사용자 ID로 생성된 유효한 JWT 토큰을 가진 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWithValidJwtForUser(String userId) {
    String validJwtToken = JwtTestHelper.generateValidToken(userId);
    return new TokenPortResponse(validJwtToken, DEFAULT_REFRESH_TOKEN);
  }

  /** 만료된 JWT 액세스 토큰을 가진 토큰 포트 응답 생성 */
  public static TokenPortResponse aTokenPortResponseWithExpiredJwt() {
    String expiredJwtToken = JwtTestHelper.generateExpiredToken(DEFAULT_USER_ID);
    return new TokenPortResponse(expiredJwtToken, DEFAULT_REFRESH_TOKEN);
  }

  /** 기본 유효한 JWT 토큰 생성 */
  public static String aValidJwtToken() {
    return JwtTestHelper.generateValidToken(DEFAULT_USER_ID);
  }

  /** 특정 사용자 ID를 가진 유효한 JWT 토큰 생성 */
  public static String aValidJwtTokenForUser(String userId) {
    return JwtTestHelper.generateValidToken(userId);
  }

  /** 만료된 JWT 토큰 생성 */
  public static String anExpiredJwtToken() {
    return JwtTestHelper.generateExpiredToken(DEFAULT_USER_ID);
  }

  /** 특정 사용자 ID를 가진 만료된 JWT 토큰 생성 */
  public static String anExpiredJwtTokenForUser(String userId) {
    return JwtTestHelper.generateExpiredToken(userId);
  }

  /** 곧 만료될 JWT 토큰 생성 (5분 후 만료) */
  public static String aSoonToExpireJwtToken() {
    return JwtTestHelper.generateSoonToExpireToken(DEFAULT_USER_ID);
  }

  /** 장기간 유효한 JWT 토큰 생성 (24시간 유효) */
  public static String aLongLivedJwtToken() {
    return JwtTestHelper.generateLongLivedToken(DEFAULT_USER_ID);
  }

  /** userId 클레임이 없는 잘못된 JWT 토큰 생성 */
  public static String aJwtTokenWithoutUserId() {
    return JwtTestHelper.generateTokenWithoutUserId();
  }

  /** 잘못된 서명을 가진 JWT 토큰 생성 */
  public static String aJwtTokenWithWrongSignature() {
    return JwtTestHelper.generateTokenWithWrongSecret(DEFAULT_USER_ID);
  }

  /** 카카오 액세스 토큰 생성 */
  public static String aKakaoAccessToken() {
    return DEFAULT_KAKAO_ACCESS_TOKEN;
  }

  /** 커스텀 카카오 액세스 토큰 생성 */
  public static String aKakaoAccessTokenWith(String token) {
    return token;
  }

  /** 잘못된 형식의 토큰 생성 (JWT가 아님) */
  public static String anInvalidFormatToken() {
    return "this-is-not-a-jwt-token";
  }

  /** null 토큰 반환 */
  public static String aNullToken() {
    return null;
  }

  /** 빈 토큰 반환 */
  public static String anEmptyToken() {
    return "";
  }
}
