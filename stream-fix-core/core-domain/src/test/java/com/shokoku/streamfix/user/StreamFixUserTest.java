package com.shokoku.streamfix.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StreamFixUser 도메인 테스트")
class StreamFixUserTest {

  @Nested
  @DisplayName("StreamFixUser 객체 생성 테스트")
  class CreateStreamFixUserTest {

    @Test
    @DisplayName("모든 필드가 정상적으로 입력된 경우 StreamFixUser 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithAllValidFields() {
      // given
      String userId = "user-123";
      String userName = "testuser";
      String encryptedPassword = "encrypted-password-hash";
      String email = "test@streamfix.com";
      String phone = "010-1234-5678";
      String provider = "local";
      String providerId = "local-123";
      String role = "USER";

      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId(userId)
              .userName(userName)
              .encryptedPassword(encryptedPassword)
              .email(email)
              .phone(phone)
              .provider(provider)
              .providerId(providerId)
              .role(role)
              .build();

      // then
      assertThat(user.userId()).isEqualTo(userId);
      assertThat(user.userName()).isEqualTo(userName);
      assertThat(user.encryptedPassword()).isEqualTo(encryptedPassword);
      assertThat(user.email()).isEqualTo(email);
      assertThat(user.phone()).isEqualTo(phone);
      assertThat(user.provider()).isEqualTo(provider);
      assertThat(user.providerId()).isEqualTo(providerId);
      assertThat(user.role()).isEqualTo(role);
    }

    @Test
    @DisplayName("빌더 패턴을 사용하여 필요한 필드만으로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithRequiredFieldsOnly() {
      // given & when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-456")
              .userName("minimaluser")
              .email("minimal@test.com")
              .build();

      // then
      assertThat(user.userId()).isEqualTo("user-456");
      assertThat(user.userName()).isEqualTo("minimaluser");
      assertThat(user.email()).isEqualTo("minimal@test.com");
      assertThat(user.encryptedPassword()).isNull();
      assertThat(user.phone()).isNull();
      assertThat(user.provider()).isNull();
      assertThat(user.providerId()).isNull();
      assertThat(user.role()).isNull();
    }

    @Test
    @DisplayName("OAuth 사용자 정보로 StreamFixUser 객체를 생성할 수 있다")
    void shouldCreateOAuthStreamFixUser() {
      // given & when
      StreamFixUser oauthUser =
          StreamFixUser.builder()
              .userId("oauth-user-789")
              .userName("oauthuser")
              .email("oauth@gmail.com")
              .provider("google")
              .providerId("google-123456789")
              .role("USER")
              .build();

      // then
      assertThat(oauthUser.provider()).isEqualTo("google");
      assertThat(oauthUser.providerId()).isEqualTo("google-123456789");
      assertThat(oauthUser.encryptedPassword()).isNull(); // OAuth 사용자는 비밀번호가 없을 수 있음
    }
  }

  @Nested
  @DisplayName("StreamFixUser 필드 검증 테스트")
  class StreamFixUserFieldValidationTest {

    @ParameterizedTest(name = "사용자명 \"{0}\"으로 객체를 생성할 수 있다")
    @ValueSource(
        strings = {
          "user123",
          "한글사용자",
          "user_with_underscore",
          "user-with-dash",
          "UserWithCamelCase",
          "사용자@도메인"
        })
    @DisplayName("다양한 형식의 사용자명으로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithVariousUserNames(String userName) {
      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-id")
              .userName(userName)
              .email("test@example.com")
              .build();

      // then
      assertThat(user.userName()).isEqualTo(userName);
    }

    @ParameterizedTest(name = "이메일 \"{0}\"으로 객체를 생성할 수 있다")
    @ValueSource(
        strings = {
          "test@example.com",
          "user.name@domain.co.kr",
          "admin@streamfix.com",
          "user+tag@gmail.com",
          "very.long.email.address@very.long.domain.name.com"
        })
    @DisplayName("다양한 형식의 이메일로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithVariousEmails(String email) {
      // when
      StreamFixUser user =
          StreamFixUser.builder().userId("user-id").userName("testuser").email(email).build();

      // then
      assertThat(user.email()).isEqualTo(email);
    }

    @ParameterizedTest(name = "전화번호 \"{0}\"으로 객체를 생성할 수 있다")
    @ValueSource(
        strings = {
          "010-1234-5678",
          "02-123-4567",
          "031-987-6543",
          "+82-10-1234-5678",
          "01012345678"
        })
    @DisplayName("다양한 형식의 전화번호로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithVariousPhoneNumbers(String phone) {
      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-id")
              .userName("testuser")
              .email("test@example.com")
              .phone(phone)
              .build();

      // then
      assertThat(user.phone()).isEqualTo(phone);
    }

    @ParameterizedTest(name = "제공자 \"{0}\"으로 객체를 생성할 수 있다")
    @ValueSource(strings = {"local", "google", "kakao", "naver", "facebook", "github"})
    @DisplayName("다양한 OAuth 제공자로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithVariousProviders(String provider) {
      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-id")
              .userName("testuser")
              .email("test@example.com")
              .provider(provider)
              .providerId(provider + "-123")
              .build();

      // then
      assertThat(user.provider()).isEqualTo(provider);
      assertThat(user.providerId()).isEqualTo(provider + "-123");
    }

    @ParameterizedTest(name = "역할 \"{0}\"으로 객체를 생성할 수 있다")
    @ValueSource(strings = {"USER", "ADMIN", "MANAGER", "PREMIUM_USER", "GUEST"})
    @DisplayName("다양한 사용자 역할로 객체를 생성할 수 있다")
    void shouldCreateStreamFixUserWithVariousRoles(String role) {
      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-id")
              .userName("testuser")
              .email("test@example.com")
              .role(role)
              .build();

      // then
      assertThat(user.role()).isEqualTo(role);
    }
  }

  @Nested
  @DisplayName("StreamFixUser null 값 처리 테스트")
  class StreamFixUserNullHandlingTest {

    @ParameterizedTest(name = "null 또는 빈 값 \"{0}\"도 허용한다")
    @NullAndEmptySource
    @DisplayName("선택적 필드들은 null 또는 빈 값을 허용한다")
    void shouldAllowNullOrEmptyOptionalFields(String value) {
      // when
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-id")
              .userName("testuser")
              .email("test@example.com")
              .encryptedPassword(value)
              .phone(value)
              .provider(value)
              .providerId(value)
              .role(value)
              .build();

      // then
      assertThat(user.userId()).isEqualTo("user-id");
      assertThat(user.userName()).isEqualTo("testuser");
      assertThat(user.email()).isEqualTo("test@example.com");

      if (value == null) {
        assertThat(user.encryptedPassword()).isNull();
        assertThat(user.phone()).isNull();
        assertThat(user.provider()).isNull();
        assertThat(user.providerId()).isNull();
        assertThat(user.role()).isNull();
      } else {
        assertThat(user.encryptedPassword()).isEmpty();
        assertThat(user.phone()).isEmpty();
        assertThat(user.provider()).isEmpty();
        assertThat(user.providerId()).isEmpty();
        assertThat(user.role()).isEmpty();
      }
    }
  }

  @Nested
  @DisplayName("StreamFixUser 비즈니스 로직 테스트")
  class StreamFixUserBusinessLogicTest {

    @Test
    @DisplayName("로컬 사용자인지 확인할 수 있다")
    void shouldIdentifyLocalUser() {
      // given
      StreamFixUser localUser =
          StreamFixUser.builder()
              .userId("local-user")
              .userName("localuser")
              .email("local@test.com")
              .encryptedPassword("encrypted-password")
              .provider("local")
              .build();

      StreamFixUser oauthUser =
          StreamFixUser.builder()
              .userId("oauth-user")
              .userName("oauthuser")
              .email("oauth@test.com")
              .provider("google")
              .build();

      // when & then
      assertThat(isLocalUser(localUser)).isTrue();
      assertThat(isLocalUser(oauthUser)).isFalse();
    }

    @Test
    @DisplayName("OAuth 사용자인지 확인할 수 있다")
    void shouldIdentifyOAuthUser() {
      // given
      StreamFixUser googleUser =
          StreamFixUser.builder()
              .userId("google-user")
              .userName("googleuser")
              .email("google@gmail.com")
              .provider("google")
              .providerId("google-123")
              .build();

      StreamFixUser localUser =
          StreamFixUser.builder()
              .userId("local-user")
              .userName("localuser")
              .email("local@test.com")
              .provider("local")
              .build();

      // when & then
      assertThat(isOAuthUser(googleUser)).isTrue();
      assertThat(isOAuthUser(localUser)).isFalse();
    }

    @Test
    @DisplayName("관리자 권한을 가진 사용자인지 확인할 수 있다")
    void shouldIdentifyAdminUser() {
      // given
      StreamFixUser adminUser =
          StreamFixUser.builder()
              .userId("admin-user")
              .userName("admin")
              .email("admin@streamfix.com")
              .role("ADMIN")
              .build();

      StreamFixUser regularUser =
          StreamFixUser.builder()
              .userId("regular-user")
              .userName("user")
              .email("user@streamfix.com")
              .role("USER")
              .build();

      // when & then
      assertThat(isAdmin(adminUser)).isTrue();
      assertThat(isAdmin(regularUser)).isFalse();
    }

    @Test
    @DisplayName("사용자의 표시명을 생성할 수 있다")
    void shouldGenerateDisplayName() {
      // given
      StreamFixUser user =
          StreamFixUser.builder()
              .userId("user-123")
              .userName("testuser")
              .email("test@example.com")
              .build();

      // when
      String displayName = generateDisplayName(user);

      // then
      assertThat(displayName).isEqualTo("testuser (test@example.com)");
    }

    @Test
    @DisplayName("비밀번호가 설정되어 있는지 확인할 수 있다")
    void shouldCheckIfPasswordIsSet() {
      // given
      StreamFixUser userWithPassword =
          StreamFixUser.builder()
              .userId("user-with-pw")
              .userName("user1")
              .email("user1@test.com")
              .encryptedPassword("encrypted-password")
              .build();

      StreamFixUser userWithoutPassword =
          StreamFixUser.builder()
              .userId("user-without-pw")
              .userName("user2")
              .email("user2@test.com")
              .build();

      // when & then
      assertThat(hasPassword(userWithPassword)).isTrue();
      assertThat(hasPassword(userWithoutPassword)).isFalse();
    }

    // 헬퍼 메서드들 (실제 비즈니스 로직 시뮬레이션)
    private boolean isLocalUser(StreamFixUser user) {
      return "local".equals(user.provider()) || user.provider() == null;
    }

    private boolean isOAuthUser(StreamFixUser user) {
      return user.provider() != null && !"local".equals(user.provider());
    }

    private boolean isAdmin(StreamFixUser user) {
      return "ADMIN".equals(user.role());
    }

    private String generateDisplayName(StreamFixUser user) {
      return user.userName() + " (" + user.email() + ")";
    }

    private boolean hasPassword(StreamFixUser user) {
      return user.encryptedPassword() != null && !user.encryptedPassword().trim().isEmpty();
    }
  }

  @Nested
  @DisplayName("StreamFixUser 동등성 및 해시코드 테스트")
  class StreamFixUserEqualityTest {

    @Test
    @DisplayName("동일한 정보를 가진 두 StreamFixUser 객체는 같다")
    void shouldBeEqualWhenSameContent() {
      // given
      StreamFixUser user1 =
          StreamFixUser.builder()
              .userId("same-id")
              .userName("sameuser")
              .email("same@test.com")
              .encryptedPassword("same-password")
              .phone("010-1234-5678")
              .provider("local")
              .providerId("local-123")
              .role("USER")
              .build();

      StreamFixUser user2 =
          StreamFixUser.builder()
              .userId("same-id")
              .userName("sameuser")
              .email("same@test.com")
              .encryptedPassword("same-password")
              .phone("010-1234-5678")
              .provider("local")
              .providerId("local-123")
              .role("USER")
              .build();

      // when & then
      assertThat(user1).isEqualTo(user2);
      assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("다른 정보를 가진 두 StreamFixUser 객체는 다르다")
    void shouldNotBeEqualWhenDifferentContent() {
      // given
      StreamFixUser user1 =
          StreamFixUser.builder()
              .userId("user-1")
              .userName("user1")
              .email("user1@test.com")
              .build();

      StreamFixUser user2 =
          StreamFixUser.builder()
              .userId("user-2")
              .userName("user2")
              .email("user2@test.com")
              .build();

      // when & then
      assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("사용자 ID만 다른 경우 다른 객체로 인식된다")
    void shouldNotBeEqualWhenDifferentUserId() {
      // given
      StreamFixUser user1 =
          StreamFixUser.builder()
              .userId("user-1")
              .userName("sameuser")
              .email("same@test.com")
              .build();

      StreamFixUser user2 =
          StreamFixUser.builder()
              .userId("user-2")
              .userName("sameuser")
              .email("same@test.com")
              .build();

      // when & then
      assertThat(user1).isNotEqualTo(user2);
    }
  }

  @Nested
  @DisplayName("StreamFixUser 실제 사용 시나리오 테스트")
  class StreamFixUserRealWorldScenarioTest {

    @ParameterizedTest(name = "사용자 시나리오: {0}")
    @MethodSource("provideUserScenarios")
    @DisplayName("실제 사용 시나리오에서 적절한 StreamFixUser 객체를 생성할 수 있다")
    void shouldCreateAppropriateUserForRealWorldScenarios(
        String scenarioName, StreamFixUser expectedUser) {
      // when & then
      assertThat(expectedUser).isNotNull();
      assertThat(expectedUser.userId()).isNotNull();
      assertThat(expectedUser.userName()).isNotNull();
      assertThat(expectedUser.email()).isNotNull();

      // 시나리오별 특성 검증
      if (scenarioName.contains("OAuth")) {
        assertThat(expectedUser.provider()).isNotNull();
        assertThat(expectedUser.providerId()).isNotNull();
      } else if (scenarioName.contains("로컬")) {
        assertThat(expectedUser.encryptedPassword()).isNotNull();
      }

      if (scenarioName.contains("관리자")) {
        assertThat(expectedUser.role()).contains("ADMIN");
      }
    }

    private static Stream<Arguments> provideUserScenarios() {
      return Stream.of(
          Arguments.of(
              "로컬 회원가입 사용자",
              StreamFixUser.builder()
                  .userId("local-signup-001")
                  .userName("신규사용자")
                  .email("newuser@streamfix.com")
                  .encryptedPassword("$2a$10$encrypted.password.hash")
                  .phone("010-1234-5678")
                  .provider("local")
                  .role("USER")
                  .build()),
          Arguments.of(
              "Google OAuth 사용자",
              StreamFixUser.builder()
                  .userId("google-oauth-001")
                  .userName("Google User")
                  .email("googleuser@gmail.com")
                  .provider("google")
                  .providerId("google-12345678901234567890")
                  .role("USER")
                  .build()),
          Arguments.of(
              "카카오 OAuth 사용자",
              StreamFixUser.builder()
                  .userId("kakao-oauth-001")
                  .userName("카카오사용자")
                  .email("kakaouser@kakao.com")
                  .provider("kakao")
                  .providerId("kakao-987654321")
                  .role("USER")
                  .build()),
          Arguments.of(
              "시스템 관리자",
              StreamFixUser.builder()
                  .userId("admin-001")
                  .userName("관리자")
                  .email("admin@streamfix.com")
                  .encryptedPassword("$2a$10$admin.encrypted.password")
                  .phone("02-1234-5678")
                  .provider("local")
                  .role("ADMIN")
                  .build()),
          Arguments.of(
              "프리미엄 구독 사용자",
              StreamFixUser.builder()
                  .userId("premium-001")
                  .userName("프리미엄사용자")
                  .email("premium@streamfix.com")
                  .encryptedPassword("$2a$10$premium.password")
                  .phone("010-9999-8888")
                  .provider("local")
                  .role("PREMIUM_USER")
                  .build()));
    }
  }
}
