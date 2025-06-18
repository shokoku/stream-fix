package com.shokoku.streamfix.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.exception.UserException;
import com.shokoku.streamfix.fixtures.UserFixtures;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import com.shokoku.streamfix.user.response.UserResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks UserService sut;

  @Mock FetchUserPort fetchUserPort;
  @Mock InsertUserPort insertUserPort;
  @Mock KakaoUserPort kakaoUserPort;

  @Nested
  @DisplayName("fetchUserByEmail: 이메일로 사용자 조회")
  class FetchUserByEmail {

    final String existingEmail = "test@example.com";
    final String nonExistingEmail = "notfound@example.com";

    @DisplayName("실패: 이메일이 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidEmail) {
      // given
      when(fetchUserPort.findByEmail(invalidEmail)).thenReturn(Optional.empty());

      // when & then
      assertThrows(
          UserException.UserDoesNotExistException.class, () -> sut.fetchUserByEmail(invalidEmail));
    }

    @DisplayName("실패: 이메일로 사용자를 찾지 못하면 UserDoesNotExistException을 던진다")
    @Test
    void test2() {
      // given
      when(fetchUserPort.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

      // when & then
      assertThrows(
          UserException.UserDoesNotExistException.class,
          () -> sut.fetchUserByEmail(nonExistingEmail));
      verify(fetchUserPort).findByEmail(nonExistingEmail);
    }

    @DisplayName("실패: 포트에서 예외가 발생하면 해당 예외를 그대로 전파한다")
    @Test
    void test3() {
      // given
      when(fetchUserPort.findByEmail(existingEmail))
          .thenThrow(new RuntimeException("Database connection failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchUserByEmail(existingEmail));
    }

    @DisplayName("성공: 이메일로 사용자를 성공적으로 찾으면 UserResponse를 반환한다")
    @Test
    void test1000() {
      // given
      UserPortResponse mockPortResponse = UserFixtures.aUserWithEmail(existingEmail);
      when(fetchUserPort.findByEmail(existingEmail)).thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.fetchUserByEmail(existingEmail);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USER_ID, result.userId());
      assertEquals(existingEmail, result.email());
      assertEquals(UserFixtures.DEFAULT_PASSWORD, result.password());
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(UserFixtures.DEFAULT_ROLE, result.role());
      verify(fetchUserPort).findByEmail(existingEmail);
    }

    @DisplayName("성공: 일부 필드가 null인 사용자도 정상적으로 반환한다")
    @Test
    void test1001() {
      // given
      UserPortResponse mockPortResponse =
          UserFixtures.aUserBuilder()
              .userId("user456")
              .username("testUser2")
              .password(null)
              .email(existingEmail)
              .phone(null)
              .role("ADMIN")
              .build();
      when(fetchUserPort.findByEmail(existingEmail)).thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.fetchUserByEmail(existingEmail);

      // then
      assertNotNull(result);
      assertEquals("user456", result.userId());
      assertEquals(existingEmail, result.email());
      assertNull(result.password());
      assertEquals("testUser2", result.username());
      assertEquals("ADMIN", result.role());
    }
  }

  @Nested
  @DisplayName("findByProviderId: ProviderId로 사용자 조회")
  class FindByProviderId {

    final String providerId = "kakao123";
    final String nonExistingProviderId = "nonexistent456";

    @DisplayName("실패: ProviderId가 null이거나 빈 값이면 null을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidProviderId) {
      // given
      when(fetchUserPort.findByProviderId(invalidProviderId)).thenReturn(Optional.empty());

      // when
      UserResponse result = sut.findByProviderId(invalidProviderId);

      // then
      assertNull(result);
    }

    @DisplayName("실패: ProviderId로 사용자를 찾지 못하면 null을 반환한다")
    @Test
    void test2() {
      // given
      when(fetchUserPort.findByProviderId(nonExistingProviderId)).thenReturn(Optional.empty());

      // when
      UserResponse result = sut.findByProviderId(nonExistingProviderId);

      // then
      assertNull(result);
      verify(fetchUserPort).findByProviderId(nonExistingProviderId);
    }

    @DisplayName("실패: 포트에서 예외가 발생하면 해당 예외를 그대로 전파한다")
    @Test
    void test3() {
      // given
      when(fetchUserPort.findByProviderId(providerId))
          .thenThrow(new RuntimeException("Database connection failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findByProviderId(providerId));
    }

    @DisplayName("성공: ProviderId로 사용자를 성공적으로 찾으면 UserResponse를 반환한다")
    @Test
    void test1000() {
      // given
      UserPortResponse mockPortResponse = UserFixtures.aKakaoUserWithProviderId(providerId);
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.findByProviderId(providerId);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USER_ID, result.userId());
      assertEquals(providerId, result.providerId());
      assertEquals(UserFixtures.KAKAO_PROVIDER, result.provider());
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(UserFixtures.DEFAULT_ROLE, result.role());
      verify(fetchUserPort).findByProviderId(providerId);
    }

    @DisplayName("성공: 다양한 provider 타입의 사용자를 조회할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "NAVER", "APPLE"})
    void test1001(String provider) {
      // given
      String testProviderId = provider.toLowerCase() + "123";
      UserPortResponse mockPortResponse =
          UserFixtures.aUserBuilder()
              .userId("user789")
              .username("socialUser")
              .password(null)
              .email(null)
              .phone(null)
              .provider(provider)
              .providerId(testProviderId)
              .role(UserFixtures.DEFAULT_ROLE)
              .build();
      when(fetchUserPort.findByProviderId(testProviderId))
          .thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.findByProviderId(testProviderId);

      // then
      assertNotNull(result);
      assertEquals(provider, result.provider());
      assertEquals(testProviderId, result.providerId());
    }
  }

  @Nested
  @DisplayName("register: 사용자 회원가입")
  class Register {

    final String email = "test@example.com";

    @DisplayName("실패: 커맨드가 null이면 NullPointerException을 던진다")
    @Test
    void test1() {
      // when & then
      assertThrows(NullPointerException.class, () -> sut.register(null));
    }

    @DisplayName("실패: 이미 존재하는 이메일로 회원가입하면 UserAlreadyExistException을 던진다")
    @Test
    void test2() {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommandWithEmail(email);
      UserPortResponse existingUser = UserFixtures.aUserWithEmail(email);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.of(existingUser));

      // when & then
      assertThrows(UserException.UserAlreadyExistException.class, () -> sut.register(command));
      verify(fetchUserPort).findByEmail(email);
      verify(insertUserPort, never()).create(any());
    }

    @DisplayName("실패: 사용자 생성 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test3() {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommandWithEmail(email);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());
      when(insertUserPort.create(any(CreateUser.class)))
          .thenThrow(new RuntimeException("Database insertion failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.register(command));
      verify(fetchUserPort).findByEmail(email);
      verify(insertUserPort).create(any(CreateUser.class));
    }

    @DisplayName("성공: 새로운 사용자를 성공적으로 등록한다")
    @Test
    void test1000() {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommandWithEmail(email);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          UserFixtures.aUserBuilder().userId("newUser123").email(email).build();
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(email, result.email());
      assertEquals(UserFixtures.DEFAULT_PHONE, result.phone());
      verify(fetchUserPort).findByEmail(email);
      verify(insertUserPort).create(any(CreateUser.class));
    }

    @DisplayName("성공: 전화번호가 없는 사용자도 등록할 수 있다")
    @Test
    void test1001() {
      // given
      UserRegisterCommand command =
          UserRegisterCommand.builder()
              .username(UserFixtures.DEFAULT_USERNAME)
              .encryptedPassword(UserFixtures.DEFAULT_PASSWORD)
              .email(email)
              .phone(null)
              .build();
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          UserFixtures.aUserBuilder().userId("newUser456").email(email).phone(null).build();
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(email, result.email());
      assertNull(result.phone());
    }

    @DisplayName("성공: 다양한 역할의 사용자를 등록할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "MODERATOR"})
    void test1002(String role) {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommandWithEmail(email);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          UserFixtures.aUserBuilder().userId("roleUser123").email(email).role(role).build();
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(email, result.email());
    }
  }

  @Nested
  @DisplayName("registerSocialUser: 소셜 사용자 회원가입")
  class RegisterSocialUser {

    final String username = "socialUser";
    final String provider = UserFixtures.KAKAO_PROVIDER;
    final String providerId = UserFixtures.DEFAULT_KAKAO_PROVIDER_ID;

    @DisplayName("실패: username이 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidUsername) {
      // given
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.empty());
      when(insertUserPort.createSocialUser(invalidUsername, provider, providerId))
          .thenThrow(new IllegalArgumentException("Invalid username"));

      // when & then
      assertThrows(
          IllegalArgumentException.class,
          () -> sut.registerSocialUser(invalidUsername, provider, providerId));
    }

    @DisplayName("실패: provider가 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test2(String invalidProvider) {
      // given
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.empty());
      when(insertUserPort.createSocialUser(username, invalidProvider, providerId))
          .thenThrow(new IllegalArgumentException("Invalid provider"));

      // when & then
      assertThrows(
          IllegalArgumentException.class,
          () -> sut.registerSocialUser(username, invalidProvider, providerId));
    }

    @DisplayName("실패: providerId가 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test3(String invalidProviderId) {
      // given
      when(fetchUserPort.findByProviderId(invalidProviderId)).thenReturn(Optional.empty());
      when(insertUserPort.createSocialUser(username, provider, invalidProviderId))
          .thenThrow(new IllegalArgumentException("Invalid providerId"));

      // when & then
      assertThrows(
          IllegalArgumentException.class,
          () -> sut.registerSocialUser(username, provider, invalidProviderId));
    }

    @DisplayName("실패: 소셜 사용자 생성 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test4() {
      // given
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.empty());
      when(insertUserPort.createSocialUser(username, provider, providerId))
          .thenThrow(new RuntimeException("Database insertion failed"));

      // when & then
      assertThrows(
          RuntimeException.class, () -> sut.registerSocialUser(username, provider, providerId));
    }

    @DisplayName("성공: 이미 존재하는 소셜 사용자는 null을 반환한다")
    @Test
    void test1000() {
      // given
      UserPortResponse existingUser =
          UserFixtures.aKakaoUserBuilder().userId("existingUser").username(username).build();
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.of(existingUser));

      // when
      UserRegisterResponse result = sut.registerSocialUser(username, provider, providerId);

      // then
      assertNull(result);
      verify(fetchUserPort).findByProviderId(providerId);
      verify(insertUserPort, never()).createSocialUser(anyString(), anyString(), anyString());
    }

    @DisplayName("성공: 새로운 소셜 사용자를 성공적으로 등록한다")
    @Test
    void test1001() {
      // given
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.empty());

      UserPortResponse createdSocialUser =
          UserFixtures.aKakaoUserBuilder().userId("socialUser123").username(username).build();
      when(insertUserPort.createSocialUser(username, provider, providerId))
          .thenReturn(createdSocialUser);

      // when
      UserRegisterResponse result = sut.registerSocialUser(username, provider, providerId);

      // then
      assertNotNull(result);
      assertEquals(username, result.username());
      assertNull(result.email());
      assertNull(result.phone());
      verify(fetchUserPort).findByProviderId(providerId);
      verify(insertUserPort).createSocialUser(username, provider, providerId);
    }

    @DisplayName("성공: 다양한 소셜 provider로 사용자를 등록할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "NAVER", "APPLE", "FACEBOOK"})
    void test1002(String socialProvider) {
      // given
      String testProviderId = socialProvider.toLowerCase() + "123";
      when(fetchUserPort.findByProviderId(testProviderId)).thenReturn(Optional.empty());

      UserPortResponse createdSocialUser =
          UserFixtures.aUserBuilder()
              .userId("socialUser456")
              .username(username)
              .password(null)
              .email(null)
              .phone(null)
              .provider(socialProvider)
              .providerId(testProviderId)
              .role(UserFixtures.DEFAULT_ROLE)
              .build();
      when(insertUserPort.createSocialUser(username, socialProvider, testProviderId))
          .thenReturn(createdSocialUser);

      // when
      UserRegisterResponse result =
          sut.registerSocialUser(username, socialProvider, testProviderId);

      // then
      assertNotNull(result);
      assertEquals(username, result.username());
      verify(insertUserPort).createSocialUser(username, socialProvider, testProviderId);
    }
  }

  @Nested
  @DisplayName("findKakaoUser: 카카오 사용자 조회")
  class FindKakaoUser {

    final String accessToken = "kakaoAccessToken";

    @DisplayName("실패: accessToken이 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidToken) {
      // given
      when(kakaoUserPort.findUserFromKakao(invalidToken))
          .thenThrow(new IllegalArgumentException("Invalid access token"));

      // when & then
      assertThrows(IllegalArgumentException.class, () -> sut.findKakaoUser(invalidToken));
    }

    @DisplayName("실패: 카카오 API 호출 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      when(kakaoUserPort.findUserFromKakao(accessToken))
          .thenThrow(new RuntimeException("Kakao API 에러"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findKakaoUser(accessToken));
      verify(kakaoUserPort).findUserFromKakao(accessToken);
    }

    @DisplayName("실패: 잘못된 토큰으로 인증에 실패하면 관련 Exception을 던진다")
    @Test
    void test3() {
      // given
      String invalidToken = "invalidKakaoToken";
      when(kakaoUserPort.findUserFromKakao(invalidToken))
          .thenThrow(new RuntimeException("Unauthorized"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findKakaoUser(invalidToken));
    }

    @DisplayName("성공: 카카오 사용자 정보를 성공적으로 가져온다")
    @Test
    void test1000() {
      // given
      UserPortResponse kakaoUserResponse =
          UserFixtures.aKakaoUserBuilder()
              .userId(null)
              .username("kakaoUser")
              .providerId("kakao123")
              .role(null)
              .build();
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.KAKAO_PROVIDER, result.provider());
      assertEquals("kakao123", result.providerId());
      assertEquals("kakaoUser", result.username());
      verify(kakaoUserPort).findUserFromKakao(accessToken);
    }

    @DisplayName("성공: 카카오 사용자 정보에 userId가 있어도 정상 처리한다")
    @Test
    void test1001() {
      // given
      UserPortResponse kakaoUserResponse =
          UserFixtures.aKakaoUserBuilder()
              .userId("user789")
              .username("kakaoUser2")
              .providerId("kakao456")
              .build();
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.KAKAO_PROVIDER, result.provider());
      assertEquals("kakao456", result.providerId());
      assertEquals("kakaoUser2", result.username());
    }

    @DisplayName("성공: 카카오 응답에서 일부 필드가 null이어도 정상 처리한다")
    @Test
    void test1002() {
      // given
      UserPortResponse kakaoUserResponse =
          UserFixtures.aKakaoUserBuilder()
              .userId(null)
              .username(null)
              .providerId("kakao789")
              .role(null)
              .build();
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.KAKAO_PROVIDER, result.provider());
      assertEquals("kakao789", result.providerId());
      assertNull(result.username());
    }
  }

  @Nested
  @DisplayName("UserFixtures Integration: 다양한 사용자 픽스처 활용 테스트")
  class UserFixturesIntegration {

    @DisplayName("기본 사용자 픽스처를 활용한 조회 테스트")
    @Test
    void test1() {
      // given
      UserPortResponse defaultUser = UserFixtures.aUser();
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL))
          .thenReturn(Optional.of(defaultUser));

      // when
      UserResponse result = sut.fetchUserByEmail(UserFixtures.DEFAULT_EMAIL);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USER_ID, result.userId());
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(UserFixtures.DEFAULT_EMAIL, result.email());
      assertEquals(UserFixtures.DEFAULT_PASSWORD, result.password());
      assertEquals(UserFixtures.DEFAULT_ROLE, result.role());
      // fetchUserByEmail 메서드는 provider, providerId, phone 필드를 설정하지 않음
      assertNull(result.provider());
      assertNull(result.providerId());
      assertNull(result.phone());
    }

    @DisplayName("특정 ID를 가진 사용자 픽스처 테스트")
    @Test
    void test2() {
      // given
      String customUserId = "customUser456";
      UserPortResponse userWithId = UserFixtures.aUserWithId(customUserId);
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL))
          .thenReturn(Optional.of(userWithId));

      // when
      UserResponse result = sut.fetchUserByEmail(UserFixtures.DEFAULT_EMAIL);

      // then
      assertNotNull(result);
      assertEquals(customUserId, result.userId());
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
    }

    @DisplayName("특정 역할을 가진 사용자 픽스처 테스트")
    @Test
    void test3() {
      // given
      String adminRole = "ADMIN";
      UserPortResponse adminUser = UserFixtures.aUserWithRole(adminRole);
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL))
          .thenReturn(Optional.of(adminUser));

      // when
      UserResponse result = sut.fetchUserByEmail(UserFixtures.DEFAULT_EMAIL);

      // then
      assertNotNull(result);
      assertEquals(adminRole, result.role());
      assertEquals(UserFixtures.DEFAULT_USER_ID, result.userId());
    }

    @DisplayName("전화번호가 없는 사용자 픽스처 테스트")
    @Test
    void test4() {
      // given
      UserPortResponse userWithoutPhone = UserFixtures.aUserWithoutPhone();
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL))
          .thenReturn(Optional.of(userWithoutPhone));

      // when
      UserResponse result = sut.fetchUserByEmail(UserFixtures.DEFAULT_EMAIL);

      // then
      assertNotNull(result);
      assertNull(result.phone());
      assertEquals(UserFixtures.DEFAULT_EMAIL, result.email());
    }

    @DisplayName("기본 카카오 사용자 픽스처 테스트")
    @Test
    void test5() {
      // given
      UserPortResponse kakaoUser = UserFixtures.aKakaoUser();
      when(fetchUserPort.findByProviderId(UserFixtures.DEFAULT_KAKAO_PROVIDER_ID))
          .thenReturn(Optional.of(kakaoUser));

      // when
      UserResponse result = sut.findByProviderId(UserFixtures.DEFAULT_KAKAO_PROVIDER_ID);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.KAKAO_PROVIDER, result.provider());
      assertEquals(UserFixtures.DEFAULT_KAKAO_PROVIDER_ID, result.providerId());
      assertNull(result.password());
      assertNull(result.email());
      assertNull(result.phone());
    }

    @DisplayName("구글 사용자 픽스처 테스트")
    @Test
    void test6() {
      // given
      UserPortResponse googleUser = UserFixtures.aGoogleUser();
      when(fetchUserPort.findByProviderId(UserFixtures.DEFAULT_GOOGLE_PROVIDER_ID))
          .thenReturn(Optional.of(googleUser));

      // when
      UserResponse result = sut.findByProviderId(UserFixtures.DEFAULT_GOOGLE_PROVIDER_ID);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.GOOGLE_PROVIDER, result.provider());
      assertEquals(UserFixtures.DEFAULT_GOOGLE_PROVIDER_ID, result.providerId());
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
    }

    @DisplayName("커스텀 소셜 사용자 픽스처 테스트")
    @Test
    void test7() {
      // given
      String customProvider = "NAVER";
      String customProviderId = "naver123";
      UserPortResponse socialUser = UserFixtures.aSocialUser(customProvider, customProviderId);
      when(fetchUserPort.findByProviderId(customProviderId)).thenReturn(Optional.of(socialUser));

      // when
      UserResponse result = sut.findByProviderId(customProviderId);

      // then
      assertNotNull(result);
      assertEquals(customProvider, result.provider());
      assertEquals(customProviderId, result.providerId());
    }
  }

  @Nested
  @DisplayName("UserRegisterCommand Fixtures: 회원가입 커맨드 픽스처 테스트")
  class UserRegisterCommandFixtures {

    @DisplayName("기본 회원가입 커맨드 픽스처 테스트")
    @Test
    void test1() {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommand();
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL)).thenReturn(Optional.empty());

      UserPortResponse createdUser = UserFixtures.aUser();
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(UserFixtures.DEFAULT_EMAIL, result.email());
      assertEquals(UserFixtures.DEFAULT_PHONE, result.phone());
    }

    @DisplayName("전화번호가 없는 회원가입 커맨드 픽스처 테스트")
    @Test
    void test2() {
      // given
      UserRegisterCommand command = UserFixtures.aUserRegisterCommandWithoutPhone();
      when(fetchUserPort.findByEmail(UserFixtures.DEFAULT_EMAIL)).thenReturn(Optional.empty());

      UserPortResponse createdUser = UserFixtures.aUserWithoutPhone();
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(UserFixtures.DEFAULT_USERNAME, result.username());
      assertEquals(UserFixtures.DEFAULT_EMAIL, result.email());
      assertNull(result.phone());
    }
  }

  @Nested
  @DisplayName("UserResponse Fixtures: 사용자 응답 픽스처 활용 테스트")
  class UserResponseFixtures {

    @DisplayName("기본 사용자 응답 픽스처의 속성을 확인할 수 있다")
    @Test
    void test1() {
      // when
      UserResponse userResponse = UserFixtures.aUserResponse();

      // then
      assertNotNull(userResponse);
      assertEquals(UserFixtures.DEFAULT_USER_ID, userResponse.userId());
      assertEquals(UserFixtures.DEFAULT_USERNAME, userResponse.username());
      assertEquals(UserFixtures.DEFAULT_PASSWORD, userResponse.password());
      assertEquals(UserFixtures.DEFAULT_EMAIL, userResponse.email());
      assertEquals(UserFixtures.DEFAULT_PHONE, userResponse.phone());
      assertEquals(UserFixtures.DEFAULT_ROLE, userResponse.role());
      assertNull(userResponse.provider());
      assertNull(userResponse.providerId());
    }

    @DisplayName("카카오 사용자 응답 픽스처의 속성을 확인할 수 있다")
    @Test
    void test2() {
      // when
      UserResponse kakaoUserResponse = UserFixtures.aKakaoUserResponse();

      // then
      assertNotNull(kakaoUserResponse);
      assertEquals(UserFixtures.DEFAULT_USER_ID, kakaoUserResponse.userId());
      assertEquals(UserFixtures.DEFAULT_USERNAME, kakaoUserResponse.username());
      assertNull(kakaoUserResponse.password());
      assertNull(kakaoUserResponse.email());
      assertNull(kakaoUserResponse.phone());
      assertEquals(UserFixtures.KAKAO_PROVIDER, kakaoUserResponse.provider());
      assertEquals(UserFixtures.DEFAULT_KAKAO_PROVIDER_ID, kakaoUserResponse.providerId());
      assertEquals(UserFixtures.DEFAULT_ROLE, kakaoUserResponse.role());
    }

    @DisplayName("특정 providerId를 가진 사용자 응답 픽스처를 확인할 수 있다")
    @Test
    void test3() {
      // given
      String customProviderId = "customProvider123";

      // when
      UserResponse userResponseWithProviderId =
          UserFixtures.aUserResponseWithProviderId(customProviderId);

      // then
      assertNotNull(userResponseWithProviderId);
      assertEquals(customProviderId, userResponseWithProviderId.providerId());
      assertEquals(UserFixtures.DEFAULT_USER_ID, userResponseWithProviderId.userId());
      assertEquals(UserFixtures.DEFAULT_EMAIL, userResponseWithProviderId.email());
    }
  }

  @Nested
  @DisplayName("UserRegisterResponse Fixtures: 회원가입 응답 픽스처 테스트")
  class UserRegisterResponseFixtures {

    @DisplayName("기본 회원가입 응답 픽스처의 속성을 확인할 수 있다")
    @Test
    void test1() {
      // when
      UserRegisterResponse response = UserFixtures.aUserRegisterResponse();

      // then
      assertNotNull(response);
      assertEquals(UserFixtures.DEFAULT_USERNAME, response.username());
      assertEquals(UserFixtures.DEFAULT_EMAIL, response.email());
      assertEquals(UserFixtures.DEFAULT_PHONE, response.phone());
    }

    @DisplayName("특정 이메일을 가진 회원가입 응답 픽스처를 확인할 수 있다")
    @Test
    void test2() {
      // given
      String customEmail = "custom@example.com";

      // when
      UserRegisterResponse response = UserFixtures.aUserRegisterResponseWithEmail(customEmail);

      // then
      assertNotNull(response);
      assertEquals(UserFixtures.DEFAULT_USERNAME, response.username());
      assertEquals(customEmail, response.email());
      assertEquals(UserFixtures.DEFAULT_PHONE, response.phone());
    }

    @DisplayName("전화번호가 없는 회원가입 응답 픽스처를 확인할 수 있다")
    @Test
    void test3() {
      // when
      UserRegisterResponse response = UserFixtures.aUserRegisterResponseWithoutPhone();

      // then
      assertNotNull(response);
      assertEquals(UserFixtures.DEFAULT_USERNAME, response.username());
      assertEquals(UserFixtures.DEFAULT_EMAIL, response.email());
      assertNull(response.phone());
    }
  }
}
