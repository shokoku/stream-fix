package com.shokoku.streamfix.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

/** JWT 토큰 생성을 위한 테스트 유틸리티 클래스 테스트에서 다양한 시나리오의 JWT 토큰을 쉽게 생성할 수 있도록 도와줍니다. */
public class JwtTestHelper {

  public static final String TEST_SECRET =
      "dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3NlT25seUF0TGVhc3QyNTZCaXRz";

  /** 유효한 토큰 생성 (1시간 유효) */
  public static String generateValidToken(String userId) {
    Instant now = Instant.now();
    return generateToken(userId, now, now.plus(1, ChronoUnit.HOURS));
  }

  /** 만료된 토큰 생성 */
  public static String generateExpiredToken(String userId) {
    Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
    return generateToken(userId, oneDayAgo.minus(1, ChronoUnit.HOURS), oneDayAgo);
  }

  /** 곧 만료될 토큰 생성 (5분 후 만료) */
  public static String generateSoonToExpireToken(String userId) {
    Instant now = Instant.now();
    return generateToken(userId, now, now.plus(5, ChronoUnit.MINUTES));
  }

  /** 장기간 유효한 토큰 생성 (24시간 유효) */
  public static String generateLongLivedToken(String userId) {
    Instant now = Instant.now();
    return generateToken(userId, now, now.plus(24, ChronoUnit.HOURS));
  }

  /** 사용자 정의 만료 시간으로 토큰 생성 */
  public static String generateToken(String userId, Instant issuedAt, Instant expiration) {
    return generateToken(userId, issuedAt, expiration, TEST_SECRET);
  }

  /** 사용자 정의 시크릿 키로 토큰 생성 */
  public static String generateTokenWithCustomSecret(String userId, String secret) {
    Instant now = Instant.now();
    return generateToken(userId, now, now.plus(1, ChronoUnit.HOURS), secret);
  }

  /** 모든 파라미터를 사용자 정의할 수 있는 토큰 생성 메서드 */
  public static String generateToken(
      String userId, Instant issuedAt, Instant expiration, String secret) {
    byte[] keyBytes = Base64.getDecoder().decode(secret);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);

    return Jwts.builder()
        .claim("userId", userId)
        .setIssuedAt(Date.from(issuedAt))
        .setExpiration(Date.from(expiration))
        .signWith(key)
        .compact();
  }

  /** userId 클레임이 없는 토큰 생성 (테스트용) */
  public static String generateTokenWithoutUserId() {
    byte[] keyBytes = Base64.getDecoder().decode(TEST_SECRET);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);
    Instant now = Instant.now();

    return Jwts.builder()
        .claim("someOtherClaim", "value")
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
        .signWith(key)
        .compact();
  }

  /** 잘못된 시크릿으로 서명된 토큰 생성 (서명 검증 실패 테스트용) */
  public static String generateTokenWithWrongSecret(String userId) {
    String wrongSecret = "d3JvbmdTZWNyZXRLZXlGb3JUZXN0aW5nUHVycG9zZU9ubHlBdExlYXN0MjU2Qml0cw==";
    return generateTokenWithCustomSecret(userId, wrongSecret);
  }
}
