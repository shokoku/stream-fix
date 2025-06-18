package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.user.UserPortResponse;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import com.shokoku.streamfix.user.response.UserResponse;

/**
 * 사용자 관련 테스트 픽스처 클래스
 *
 * <p>사용자 도메인의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다. 정적 팩토리 메소드 패턴과 빌더 패턴을 활용하여 유연한 테스트 데이터 생성을 지원합니다.
 */
public class UserFixtures {

  // 기본 테스트 데이터 상수
  public static final String DEFAULT_USER_ID = "user123";
  public static final String DEFAULT_USERNAME = "testUser";
  public static final String DEFAULT_PASSWORD = "encryptedPassword";
  public static final String DEFAULT_EMAIL = "test@example.com";
  public static final String DEFAULT_PHONE = "010-1234-5678";
  public static final String DEFAULT_ROLE = "USER";

  // 소셜 로그인 관련 상수
  public static final String DEFAULT_KAKAO_PROVIDER_ID = "kakao123";
  public static final String DEFAULT_GOOGLE_PROVIDER_ID = "google123";
  public static final String KAKAO_PROVIDER = "KAKAO";
  public static final String GOOGLE_PROVIDER = "GOOGLE";

  /** 기본 일반 사용자 생성 */
  public static UserPortResponse aUser() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 이메일을 가진 사용자 생성 */
  public static UserPortResponse aUserWithEmail(String email) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(email)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 사용자 ID를 가진 사용자 생성 */
  public static UserPortResponse aUserWithId(String userId) {
    return UserPortResponse.builder()
        .userId(userId)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 역할을 가진 사용자 생성 */
  public static UserPortResponse aUserWithRole(String role) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(role)
        .build();
  }

  /** 전화번호가 없는 사용자 생성 */
  public static UserPortResponse aUserWithoutPhone() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(null)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 기본 카카오 소셜 사용자 생성 */
  public static UserPortResponse aKakaoUser() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(KAKAO_PROVIDER)
        .providerId(DEFAULT_KAKAO_PROVIDER_ID)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 providerId를 가진 카카오 사용자 생성 */
  public static UserPortResponse aKakaoUserWithProviderId(String providerId) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(KAKAO_PROVIDER)
        .providerId(providerId)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 기본 구글 소셜 사용자 생성 */
  public static UserPortResponse aGoogleUser() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(GOOGLE_PROVIDER)
        .providerId(DEFAULT_GOOGLE_PROVIDER_ID)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 커스텀 소셜 사용자 생성 */
  public static UserPortResponse aSocialUser(String provider, String providerId) {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(provider)
        .providerId(providerId)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 기본 사용자 회원가입 커맨드 생성 */
  public static UserRegisterCommand aUserRegisterCommand() {
    return UserRegisterCommand.builder()
        .username(DEFAULT_USERNAME)
        .encryptedPassword(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .build();
  }

  /** 특정 이메일을 가진 회원가입 커맨드 생성 */
  public static UserRegisterCommand aUserRegisterCommandWithEmail(String email) {
    return UserRegisterCommand.builder()
        .username(DEFAULT_USERNAME)
        .encryptedPassword(DEFAULT_PASSWORD)
        .email(email)
        .phone(DEFAULT_PHONE)
        .build();
  }

  /** 전화번호가 없는 회원가입 커맨드 생성 */
  public static UserRegisterCommand aUserRegisterCommandWithoutPhone() {
    return UserRegisterCommand.builder()
        .username(DEFAULT_USERNAME)
        .encryptedPassword(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(null)
        .build();
  }

  /** 기본 사용자 응답 생성 */
  public static UserResponse aUserResponse() {
    return UserResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 카카오 사용자 응답 생성 */
  public static UserResponse aKakaoUserResponse() {
    return UserResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(KAKAO_PROVIDER)
        .providerId(DEFAULT_KAKAO_PROVIDER_ID)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 특정 providerId를 가진 사용자 응답 생성 */
  public static UserResponse aUserResponseWithProviderId(String providerId) {
    return UserResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(providerId)
        .role(DEFAULT_ROLE)
        .build();
  }

  /** 기본 사용자 회원가입 응답 생성 */
  public static UserRegisterResponse aUserRegisterResponse() {
    return new UserRegisterResponse(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PHONE);
  }

  /** 특정 이메일을 가진 회원가입 응답 생성 */
  public static UserRegisterResponse aUserRegisterResponseWithEmail(String email) {
    return new UserRegisterResponse(DEFAULT_USERNAME, email, DEFAULT_PHONE);
  }

  /** 전화번호가 없는 회원가입 응답 생성 */
  public static UserRegisterResponse aUserRegisterResponseWithoutPhone() {
    return new UserRegisterResponse(DEFAULT_USERNAME, DEFAULT_EMAIL, null);
  }

  /** 커스텀 필드를 가진 UserPortResponse 생성 (빌더 헬퍼 메서드) */
  public static UserPortResponse.UserPortResponseBuilder aUserBuilder() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(DEFAULT_PASSWORD)
        .email(DEFAULT_EMAIL)
        .phone(DEFAULT_PHONE)
        .provider(null)
        .providerId(null)
        .role(DEFAULT_ROLE);
  }

  /** 커스텀 필드를 가진 카카오 사용자 빌더 */
  public static UserPortResponse.UserPortResponseBuilder aKakaoUserBuilder() {
    return UserPortResponse.builder()
        .userId(DEFAULT_USER_ID)
        .username(DEFAULT_USERNAME)
        .password(null)
        .email(null)
        .phone(null)
        .provider(KAKAO_PROVIDER)
        .providerId(DEFAULT_KAKAO_PROVIDER_ID)
        .role(DEFAULT_ROLE);
  }
}
