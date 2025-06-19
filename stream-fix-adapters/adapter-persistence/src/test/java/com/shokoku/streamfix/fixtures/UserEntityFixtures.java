package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.entity.user.SocialUserEntity;
import com.shokoku.streamfix.entity.user.UserEntity;
import com.shokoku.streamfix.user.CreateUser;
import com.shokoku.streamfix.user.UserPortResponse;

/**
 * 사용자 엔티티 관련 테스트 픽스처 클래스
 *
 * <p>UserEntity, SocialUserEntity, UserPortResponse, CreateUser의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다.
 * Repository 계층 테스트에서 사용되는 사용자 관련 객체들을 관리합니다.
 */
public class UserEntityFixtures {

  // 기본 사용자 데이터 상수
  public static final String DEFAULT_USER_ID = "testUser123";
  public static final String DEFAULT_USERNAME = "testUser";
  public static final String DEFAULT_PASSWORD = "encryptedPassword123";
  public static final String DEFAULT_EMAIL = "test@example.com";
  public static final String DEFAULT_PHONE = "010-1234-5678";

  // 커스텀 사용자 데이터 상수
  public static final String CUSTOM_USER_ID = "customUser456";
  public static final String CUSTOM_USERNAME = "customUser";
  public static final String CUSTOM_PASSWORD = "customEncryptedPassword";
  public static final String CUSTOM_EMAIL = "custom@example.com";
  public static final String CUSTOM_PHONE = "010-9876-5432";

  // 업데이트 사용자 데이터 상수
  public static final String UPDATE_USERNAME = "updatedUser";
  public static final String UPDATE_PASSWORD = "updatedPassword123";
  public static final String UPDATE_EMAIL = "updated@example.com";
  public static final String UPDATE_PHONE = "010-1111-2222";

  // 다른 사용자 데이터 상수
  public static final String ANOTHER_USER_ID = "anotherUser789";
  public static final String ANOTHER_USERNAME = "anotherUser";
  public static final String ANOTHER_PASSWORD = "anotherPassword123";
  public static final String ANOTHER_EMAIL = "another@example.com";
  public static final String ANOTHER_PHONE = "010-3333-4444";

  // 존재하지 않는 사용자 데이터
  public static final String NON_EXISTENT_USER_ID = "nonExistentUser123";
  public static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
  public static final String NON_EXISTENT_PROVIDER_ID = "nonExistentProvider123";

  // 소셜 사용자 데이터 상수
  public static final String DEFAULT_SOCIAL_USER_ID = "socialUser123";
  public static final String DEFAULT_SOCIAL_USERNAME = "socialTestUser";
  public static final String DEFAULT_PROVIDER = "kakao";
  public static final String DEFAULT_PROVIDER_ID = "kakaoProvider123";

  public static final String CUSTOM_SOCIAL_USERNAME = "customSocialUser";
  public static final String CUSTOM_PROVIDER = "google";
  public static final String CUSTOM_PROVIDER_ID = "googleProvider456";

  public static final String ANOTHER_SOCIAL_USERNAME = "anotherSocialUser";
  public static final String ANOTHER_PROVIDER = "naver";
  public static final String ANOTHER_PROVIDER_ID = "naverProvider789";

  // 역할 관련 상수
  public static final String DEFAULT_ROLE = "ROLE_FREE";
  public static final String ADMIN_ROLE = "ROLE_ADMIN";
  public static final String PREMIUM_ROLE = "ROLE_GOLD";
  public static final String BRONZE_ROLE = "ROLE_BRONZE";
  public static final String SILVER_ROLE = "ROLE_SILVER";
  public static final String GOLD_ROLE = "ROLE_GOLD";

  // 예외 메시지 상수
  public static final String DB_SAVE_ERROR_MESSAGE = "DB 저장 실패";
  public static final String DB_QUERY_ERROR_MESSAGE = "DB 조회 실패";
  public static final String USER_NOT_FOUND_ERROR_MESSAGE = "사용자를 찾을 수 없습니다";
  public static final String EMAIL_DUPLICATE_ERROR_MESSAGE = "이미 존재하는 이메일입니다";

  /** 기본 UserEntity 생성 */
  public static UserEntity aUserEntity() {
    return new UserEntity(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_EMAIL, DEFAULT_PHONE);
  }

  /** 특정 파라미터로 UserEntity 생성 */
  public static UserEntity aUserEntityWith(
      String username, String password, String email, String phone) {
    return new UserEntity(username, password, email, phone);
  }

  /** 커스텀 UserEntity 생성 */
  public static UserEntity aCustomUserEntity() {
    return new UserEntity(CUSTOM_USERNAME, CUSTOM_PASSWORD, CUSTOM_EMAIL, CUSTOM_PHONE);
  }

  /** 다른 UserEntity 생성 */
  public static UserEntity anotherUserEntity() {
    return new UserEntity(ANOTHER_USERNAME, ANOTHER_PASSWORD, ANOTHER_EMAIL, ANOTHER_PHONE);
  }

  /** 업데이트용 UserEntity 생성 */
  public static UserEntity anUpdatedUserEntity() {
    return new UserEntity(UPDATE_USERNAME, UPDATE_PASSWORD, UPDATE_EMAIL, UPDATE_PHONE);
  }

  /** 기본 SocialUserEntity 생성 */
  public static SocialUserEntity aSocialUserEntity() {
    return new SocialUserEntity(DEFAULT_SOCIAL_USERNAME, DEFAULT_PROVIDER, DEFAULT_PROVIDER_ID);
  }

  /** 특정 파라미터로 SocialUserEntity 생성 */
  public static SocialUserEntity aSocialUserEntityWith(
      String username, String provider, String providerId) {
    return new SocialUserEntity(username, provider, providerId);
  }

  /** 커스텀 SocialUserEntity 생성 */
  public static SocialUserEntity aCustomSocialUserEntity() {
    return new SocialUserEntity(CUSTOM_SOCIAL_USERNAME, CUSTOM_PROVIDER, CUSTOM_PROVIDER_ID);
  }

  /** 다른 SocialUserEntity 생성 */
  public static SocialUserEntity anotherSocialUserEntity() {
    return new SocialUserEntity(ANOTHER_SOCIAL_USERNAME, ANOTHER_PROVIDER, ANOTHER_PROVIDER_ID);
  }

  /** 기본 CreateUser 생성 */
  public static CreateUser aCreateUser() {
    return CreateUser.builder()
        .username(DEFAULT_USERNAME)
        .encryptedPassword(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .build();
  }

  /** 커스텀 CreateUser 생성 */
  public static CreateUser aCustomCreateUser() {
    return CreateUser.builder()
        .username(CUSTOM_USERNAME)
        .encryptedPassword(CUSTOM_PASSWORD)
        .email(CUSTOM_EMAIL)
        .phone(CUSTOM_PHONE)
        .build();
  }

  /** 특정 파라미터로 CreateUser 생성 */
  public static CreateUser aCreateUserWith(
      String username, String password, String email, String phone) {
    return CreateUser.builder()
        .username(username)
        .encryptedPassword(password)
        .email(email)
        .phone(phone)
        .build();
  }

  /** 기본 UserPortResponse 생성 */
  public static UserPortResponse aUserPortResponse() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .build();
  }

  /** 커스텀 UserPortResponse 생성 */
  public static UserPortResponse aCustomUserPortResponse() {
    return UserPortResponse.builder()
        .userId(CUSTOM_USER_ID)
        .username(CUSTOM_USERNAME)
        .password(CUSTOM_PASSWORD)
        .email(CUSTOM_EMAIL)
        .phone(CUSTOM_PHONE)
        .build();
  }

  /** 소셜 사용자 UserPortResponse 생성 */
  public static UserPortResponse aSocialUserPortResponse() {
    return UserPortResponse.builder()
        .userId(DEFAULT_SOCIAL_USER_ID)
        .username(DEFAULT_SOCIAL_USERNAME)
        .provider(DEFAULT_PROVIDER)
        .providerId(DEFAULT_PROVIDER_ID)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 역할을 가진 UserPortResponse 생성 */
  public static UserPortResponse aUserPortResponseWithRole(String role) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .role(role)
        .build();
  }

  /** 특정 이메일을 가진 UserPortResponse 생성 */
  public static UserPortResponse aUserPortResponseWithEmail(String email) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(email)
        .phone(DEFAULT_PHONE)
        .build();
  }

  /** 특정 Provider ID를 가진 소셜 UserPortResponse 생성 */
  public static UserPortResponse aSocialUserPortResponseWithProviderId(String providerId) {
    return UserPortResponse.builder()
        .userId(DEFAULT_SOCIAL_USER_ID)
        .username(DEFAULT_SOCIAL_USERNAME)
        .provider(DEFAULT_PROVIDER)
        .providerId(providerId)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 관리자 UserPortResponse 생성 */
  public static UserPortResponse anAdminUserPortResponse() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .role(ADMIN_ROLE)
        .build();
  }

  /** 골드 사용자 UserPortResponse 생성 */
  public static UserPortResponse aGoldUserPortResponse() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .role(GOLD_ROLE)
        .build();
  }

  /** 존재하지 않는 사용자 ID 반환 */
  public static String aNonExistentUserId() {
    return NON_EXISTENT_USER_ID;
  }

  /** 존재하지 않는 이메일 반환 */
  public static String aNonExistentEmail() {
    return NON_EXISTENT_EMAIL;
  }

  /** 존재하지 않는 Provider ID 반환 */
  public static String aNonExistentProviderId() {
    return NON_EXISTENT_PROVIDER_ID;
  }

  /** DB 저장 실패 메시지 반환 */
  public static String aDbSaveErrorMessage() {
    return DB_SAVE_ERROR_MESSAGE;
  }

  /** DB 조회 실패 메시지 반환 */
  public static String aDbQueryErrorMessage() {
    return DB_QUERY_ERROR_MESSAGE;
  }

  /** 사용자 찾기 실패 메시지 반환 */
  public static String aUserNotFoundErrorMessage() {
    return USER_NOT_FOUND_ERROR_MESSAGE;
  }

  /** 이메일 중복 오류 메시지 반환 */
  public static String anEmailDuplicateErrorMessage() {
    return EMAIL_DUPLICATE_ERROR_MESSAGE;
  }
}
