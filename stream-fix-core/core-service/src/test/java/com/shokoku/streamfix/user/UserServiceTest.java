package com.shokoku.streamfix.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.exception.UserException;
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
      UserPortResponse mockPortResponse =
          new UserPortResponse(
              "user123",
              "testUser",
              "password",
              existingEmail,
              "010-1234-5678",
              null,
              null,
              "USER");
      when(fetchUserPort.findByEmail(existingEmail)).thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.fetchUserByEmail(existingEmail);

      // then
      assertNotNull(result);
      assertEquals("user123", result.userId());
      assertEquals(existingEmail, result.email());
      assertEquals("password", result.password());
      assertEquals("testUser", result.username());
      assertEquals("USER", result.role());
      verify(fetchUserPort).findByEmail(existingEmail);
    }

    @DisplayName("성공: 일부 필드가 null인 사용자도 정상적으로 반환한다")
    @Test
    void test1001() {
      // given
      UserPortResponse mockPortResponse =
          new UserPortResponse(
              "user456", "testUser2", null, existingEmail, null, null, null, "ADMIN");
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
      UserPortResponse mockPortResponse =
          new UserPortResponse(
              "user123", "testUser", null, null, null, "KAKAO", providerId, "USER");
      when(fetchUserPort.findByProviderId(providerId)).thenReturn(Optional.of(mockPortResponse));

      // when
      UserResponse result = sut.findByProviderId(providerId);

      // then
      assertNotNull(result);
      assertEquals("user123", result.userId());
      assertEquals(providerId, result.providerId());
      assertEquals("KAKAO", result.provider());
      assertEquals("testUser", result.username());
      assertEquals("USER", result.role());
      verify(fetchUserPort).findByProviderId(providerId);
    }

    @DisplayName("성공: 다양한 provider 타입의 사용자를 조회할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "NAVER", "APPLE"})
    void test1001(String provider) {
      // given
      String testProviderId = provider.toLowerCase() + "123";
      UserPortResponse mockPortResponse =
          new UserPortResponse(
              "user789", "socialUser", null, null, null, provider, testProviderId, "USER");
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
    final String username = "testUser";
    final String password = "encryptedPassword";
    final String phone = "010-1234-5678";

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
      UserRegisterCommand command = new UserRegisterCommand(username, password, email, phone);
      UserPortResponse existingUser =
          new UserPortResponse(
              "existingUser", username, password, email, phone, null, null, "USER");
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
      UserRegisterCommand command = new UserRegisterCommand(username, password, email, phone);
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
      UserRegisterCommand command = new UserRegisterCommand(username, password, email, phone);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          new UserPortResponse("newUser123", username, password, email, phone, null, null, "USER");
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(username, result.username());
      assertEquals(email, result.email());
      assertEquals(phone, result.phone());
      verify(fetchUserPort).findByEmail(email);
      verify(insertUserPort).create(any(CreateUser.class));
    }

    @DisplayName("성공: 전화번호가 없는 사용자도 등록할 수 있다")
    @Test
    void test1001() {
      // given
      UserRegisterCommand command = new UserRegisterCommand(username, password, email, null);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          new UserPortResponse("newUser456", username, password, email, null, null, null, "USER");
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(username, result.username());
      assertEquals(email, result.email());
      assertNull(result.phone());
    }

    @DisplayName("성공: 다양한 역할의 사용자를 등록할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "MODERATOR"})
    void test1002(String role) {
      // given
      UserRegisterCommand command = new UserRegisterCommand(username, password, email, phone);
      when(fetchUserPort.findByEmail(email)).thenReturn(Optional.empty());

      UserPortResponse createdUser =
          new UserPortResponse("roleUser123", username, password, email, phone, null, null, role);
      when(insertUserPort.create(any(CreateUser.class))).thenReturn(createdUser);

      // when
      UserRegisterResponse result = sut.register(command);

      // then
      assertNotNull(result);
      assertEquals(username, result.username());
      assertEquals(email, result.email());
    }
  }

  @Nested
  @DisplayName("registerSocialUser: 소셜 사용자 회원가입")
  class RegisterSocialUser {
    final String username = "socialUser";
    final String provider = "KAKAO";
    final String providerId = "kakao123";

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
          new UserPortResponse(
              "existingUser", username, null, null, null, provider, providerId, "USER");
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
          new UserPortResponse(
              "socialUser123", username, null, null, null, provider, providerId, "USER");
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
          new UserPortResponse(
              "socialUser456", username, null, null, null, socialProvider, testProviderId, "USER");
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
          new UserPortResponse(null, "kakaoUser", null, null, null, "KAKAO", "kakao123", null);
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals("KAKAO", result.provider());
      assertEquals("kakao123", result.providerId());
      assertEquals("kakaoUser", result.username());
      verify(kakaoUserPort).findUserFromKakao(accessToken);
    }

    @DisplayName("성공: 카카오 사용자 정보에 userId가 있어도 정상 처리한다")
    @Test
    void test1001() {
      // given
      UserPortResponse kakaoUserResponse =
          new UserPortResponse(
              "user789", "kakaoUser2", null, null, null, "KAKAO", "kakao456", "USER");
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals("KAKAO", result.provider());
      assertEquals("kakao456", result.providerId());
      assertEquals("kakaoUser2", result.username());
    }

    @DisplayName("성공: 카카오 응답에서 일부 필드가 null이어도 정상 처리한다")
    @Test
    void test1002() {
      // given
      UserPortResponse kakaoUserResponse =
          new UserPortResponse(null, null, null, null, null, "KAKAO", "kakao789", null);
      when(kakaoUserPort.findUserFromKakao(accessToken)).thenReturn(kakaoUserResponse);

      // when
      UserResponse result = sut.findKakaoUser(accessToken);

      // then
      assertNotNull(result);
      assertEquals("KAKAO", result.provider());
      assertEquals("kakao789", result.providerId());
      assertNull(result.username());
    }
  }
}
