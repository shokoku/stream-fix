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
  public static final String DEFAULT_ACCESS_TOKEN = "test.access.token";
  public static final String DEFAULT_REFRESH_TOKEN = "test.refresh.token";
  public static final String NEW_ACCESS_TOKEN = "new.access.token";
  public static final String NEW_REFRESH_TOKEN = "new.refresh.token";
  public static final String UPDATED_ACCESS_TOKEN = "updated.access.token";
  public static final String UPDATED_REFRESH_TOKEN = "updated.refresh.token";

  // 사용자 관련 상수
  public static final String CUSTOM_USER_ID = "customUser456";
  public static final String ANOTHER_USER_ID = "anotherUser789";
  public static final String UPDATE_USER_ID = "updateUser123";
  public static final String NON_EXISTENT_USER_ID = "nonExistentUser123";

  // 토큰 관련 상수
  public static final String CUSTOM_ACCESS_TOKEN = "custom.access.token";
  public static final String CUSTOM_REFRESH_TOKEN = "custom.refresh.token";
  public static final String ANOTHER_ACCESS_TOKEN = "another.access.token";
  public static final String ANOTHER_REFRESH_TOKEN = "another.refresh.token";
  public static final String SPECIAL_ACCESS_TOKEN = "special.access.token.for.admin";
  public static final String LONG_LIVED_REFRESH_TOKEN = "long.lived.refresh.token";

  // 예외 메시지 상수
  public static final String DB_SAVE_ERROR_MESSAGE = "DB 저장 실패";
  public static final String DB_QUERY_ERROR_MESSAGE = "DB 조회 실패";

  /** 기본 TokenEntity 생성 */
  public static TokenEntity aTokenEntity() {
    return TokenEntity.newTokenEntity(DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 특정 사용자 ID를 가진 TokenEntity 생성 */
  public static TokenEntity aTokenEntityWithUserId(String userId) {
    return TokenEntity.newTokenEntity(userId, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 커스텀 사용자의 TokenEntity 생성 */
  public static TokenEntity aCustomTokenEntity() {
    return TokenEntity.newTokenEntity(CUSTOM_USER_ID, CUSTOM_ACCESS_TOKEN, CUSTOM_REFRESH_TOKEN);
  }

  /** 다른 사용자의 TokenEntity 생성 */
  public static TokenEntity anotherTokenEntity() {
    return TokenEntity.newTokenEntity(ANOTHER_USER_ID, ANOTHER_ACCESS_TOKEN, ANOTHER_REFRESH_TOKEN);
  }

  /** 만료된 TokenEntity 생성 */
  public static TokenEntity anExpiredTokenEntity() {
    LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
    return new TokenEntity(
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

  /** 커스텀 TokenPortResponse 생성 */
  public static TokenPortResponse aCustomTokenPortResponse() {
    return new TokenPortResponse(CUSTOM_ACCESS_TOKEN, CUSTOM_REFRESH_TOKEN);
  }

  /** 특별한 액세스 토큰 TokenPortResponse 생성 */
  public static TokenPortResponse aSpecialTokenPortResponse() {
    return new TokenPortResponse(SPECIAL_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);
  }

  /** 장기간 리프레시 토큰 TokenPortResponse 생성 */
  public static TokenPortResponse aLongLivedTokenPortResponse() {
    return new TokenPortResponse(DEFAULT_ACCESS_TOKEN, LONG_LIVED_REFRESH_TOKEN);
  }

  /** 존재하지 않는 사용자 ID 반환 */
  public static String aNonExistentUserId() {
    return NON_EXISTENT_USER_ID;
  }

  /** DB 저장 실패 메시지 반환 */
  public static String aDbSaveErrorMessage() {
    return DB_SAVE_ERROR_MESSAGE;
  }

  /** DB 조회 실패 메시지 반환 */
  public static String aDbQueryErrorMessage() {
    return DB_QUERY_ERROR_MESSAGE;
  }
}
