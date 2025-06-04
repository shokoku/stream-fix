package com.shokoku.streamfix.user;

import com.shokoku.streamfix.exception.UserException;
import com.shokoku.streamfix.user.command.UserRegisterCommand;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import com.shokoku.streamfix.user.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@DisplayName("UserService 사용자 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private FetchUserPort fetchUserPort;
  @Mock private InsertUserPort insertUserPort;

  @InjectMocks private UserService userService;

  @Nested
  @DisplayName("fetchUserByEmail 정상 시나리오 테스트")
  class FetchUserByEmailNormalScenarioTest {

    @Test
    @DisplayName("존재하는 이메일로 사용자 조회 시 UserResponse를 반환한다")
    void shouldReturnUserResponseWhenUserExists() {
      // given
      String email = "test@example.com";
      UserPortResponse portResponse =
          UserPortResponse.builder()
              .userId("user-id-123")
              .username("testuser")
              .password("encrypted-password")
              .email(email)
              .phone("010-1234-5678")
              .role("USER")
              .build();

      given(fetchUserPort.findByEmail(email)).willReturn(Optional.of(portResponse));

      // when
      UserResponse result = userService.fetchUserByEmail(email);

      // then
      assertThat(result).isNotNull();
      assertThat(result.userId()).isEqualTo("user-id-123");
      assertThat(result.username()).isEqualTo("testuser");
      assertThat(result.password()).isEqualTo("encrypted-password");
      assertThat(result.email()).isEqualTo(email);
      assertThat(result.role()).isEqualTo("USER");

      then(fetchUserPort).should(times(1)).findByEmail(email);
    }

    @ParameterizedTest(name = "이메일 {0}로 사용자를 조회할 수 있다")
    @ValueSource(
        strings = {
          "user@domain.com",
          "test.email@company.co.kr",
          "admin@streamfix.com",
          "user123@test.org"
        })
    @DisplayName("다양한 형식의 이메일로 사용자를 조회할 수 있다")
    void shouldFetchUserWithVariousEmailFormats(String email) {
      // given
      UserPortResponse portResponse =
          UserPortResponse.builder()
              .userId("user-id")
              .username("username")
              .password("password")
              .email(email)
              .phone("010-0000-0000")
              .role("USER")
              .build();

      given(fetchUserPort.findByEmail(email)).willReturn(Optional.of(portResponse));

      // when
      UserResponse result = userService.fetchUserByEmail(email);

      // then
      assertThat(result.email()).isEqualTo(email);
      then(fetchUserPort).should().findByEmail(email);
    }
  }

  @Nested
  @DisplayName("fetchUserByEmail 예외 시나리오 테스트")
  class FetchUserByEmailExceptionTest {

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 UserDoesNotExistException을 발생시킨다")
    void shouldThrowUserDoesNotExistExceptionWhenUserNotFound() {
      // given
      String nonExistentEmail = "nonexistent@example.com";
      given(fetchUserPort.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> userService.fetchUserByEmail(nonExistentEmail))
          .isInstanceOf(UserException.UserDoesNotExistException.class);

      then(fetchUserPort).should().findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("포트에서 예외 발생 시 그대로 전파한다")
    void shouldPropagateExceptionFromPort() {
      // given
      String email = "test@example.com";
      given(fetchUserPort.findByEmail(email))
          .willThrow(new RuntimeException("Database connection failed"));

      // when & then
      assertThatThrownBy(() -> userService.fetchUserByEmail(email))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database connection failed");

      then(fetchUserPort).should().findByEmail(email);
    }
  }

  @Nested
  @DisplayName("register 정상 시나리오 테스트")
  class RegisterNormalScenarioTest {

    @Test
    @DisplayName("새로운 사용자 등록 시 UserRegisterResponse를 반환한다")
    void shouldReturnUserRegisterResponseWhenRegisterNewUser() {
      // given
      UserRegisterCommand command =
          UserRegisterCommand.builder()
              .username("newuser")
              .encryptedPassword("encrypted-password")
              .email("newuser@example.com")
              .phone("010-9876-5432")
              .build();

      given(fetchUserPort.findByEmail(command.email())).willReturn(Optional.empty());

      UserPortResponse createdUser =
          UserPortResponse.builder()
              .userId("new-user-id")
              .username(command.username())
              .password(command.encryptedPassword())
              .email(command.email())
              .phone(command.phone())
              .build();

      given(insertUserPort.create(any(CreateUser.class))).willReturn(createdUser);

      // when
      UserRegisterResponse result = userService.register(command);

      // then
      assertThat(result).isNotNull();
      assertThat(result.username()).isEqualTo("newuser");
      assertThat(result.email()).isEqualTo("newuser@example.com");
      assertThat(result.phone()).isEqualTo("010-9876-5432");

      then(fetchUserPort).should().findByEmail(command.email());
      then(insertUserPort).should().create(any(CreateUser.class));
    }

    @ParameterizedTest(name = "사용자 정보 {0}로 등록할 수 있다")
    @MethodSource("provideUserRegistrationData")
    @DisplayName("다양한 사용자 정보로 등록할 수 있다")
    void shouldRegisterWithVariousUserData(UserRegisterCommand command, String expectedUsername) {
      // given
      given(fetchUserPort.findByEmail(command.email())).willReturn(Optional.empty());

      UserPortResponse createdUser =
          UserPortResponse.builder()
              .userId("user-id")
              .username(command.username())
              .password(command.encryptedPassword())
              .email(command.email())
              .phone(command.phone())
              .build();

      given(insertUserPort.create(any(CreateUser.class))).willReturn(createdUser);

      // when
      UserRegisterResponse result = userService.register(command);

      // then
      assertThat(result.username()).isEqualTo(expectedUsername);
      then(fetchUserPort).should().findByEmail(command.email());
      then(insertUserPort).should().create(any(CreateUser.class));
    }

    private static Stream<Arguments> provideUserRegistrationData() {
      return Stream.of(
          Arguments.of(
              UserRegisterCommand.builder()
                  .username("korean사용자")
                  .encryptedPassword("password")
                  .email("korean@test.com")
                  .phone("010-1111-2222")
                  .build(),
              "korean사용자"),
          Arguments.of(
              UserRegisterCommand.builder()
                  .username("user_with_underscore")
                  .encryptedPassword("password")
                  .email("underscore@test.com")
                  .phone("010-3333-4444")
                  .build(),
              "user_with_underscore"));
    }
  }

  @Nested
  @DisplayName("register 예외 시나리오 테스트")
  class RegisterExceptionTest {

    @Test
    @DisplayName("이미 존재하는 이메일로 등록 시 UserAllReadyExistException을 발생시킨다")
    void shouldThrowUserAllReadyExistExceptionWhenEmailAlreadyExists() {
      // given
      UserRegisterCommand command =
          UserRegisterCommand.builder()
              .username("existinguser")
              .encryptedPassword("password")
              .email("existing@example.com")
              .phone("010-0000-0000")
              .build();

      UserPortResponse existingUser =
          UserPortResponse.builder()
              .userId("existing-user-id")
              .username("existinguser")
              .email("existing@example.com")
              .build();

      given(fetchUserPort.findByEmail(command.email())).willReturn(Optional.of(existingUser));

      // when & then
      assertThatThrownBy(() -> userService.register(command))
          .isInstanceOf(UserException.UserAllReadyExistException.class);

      then(fetchUserPort).should().findByEmail(command.email());
      then(insertUserPort).should(never()).create(any(CreateUser.class));
    }

    @Test
    @DisplayName("사용자 생성 중 포트에서 예외 발생 시 그대로 전파한다")
    void shouldPropagateExceptionFromInsertPort() {
      // given
      UserRegisterCommand command =
          UserRegisterCommand.builder()
              .username("testuser")
              .encryptedPassword("password")
              .email("test@example.com")
              .phone("010-0000-0000")
              .build();

      given(fetchUserPort.findByEmail(command.email())).willReturn(Optional.empty());
      given(insertUserPort.create(any(CreateUser.class)))
          .willThrow(new RuntimeException("Database insertion failed"));

      // when & then
      assertThatThrownBy(() -> userService.register(command))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database insertion failed");

      then(fetchUserPort).should().findByEmail(command.email());
      then(insertUserPort).should().create(any(CreateUser.class));
    }
  }

  @Nested
  @DisplayName("UserService 인터페이스 구현 테스트")
  class UserServiceInterfaceTest {

    @Test
    @DisplayName("FetchUserUseCase와 RegisterUserUseCase 인터페이스를 모두 구현한다")
    void shouldImplementBothUserUseCases() {
      // when & then
      assertThat(userService).isInstanceOf(FetchUserUseCase.class);
      assertThat(userService).isInstanceOf(RegisterUserUseCase.class);
    }

    @Test
    @DisplayName("서비스 클래스가 필요한 어노테이션들을 가진다")
    void shouldHaveRequiredAnnotations() {
      // given
      Class<UserService> serviceClass = UserService.class;

      // when & then
      assertThat(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class))
          .isTrue();

      // Lombok @RequiredArgsConstructor가 생성한 생성자 확인
      assertThat(serviceClass.getDeclaredConstructors()).hasSize(1);
      assertThat(serviceClass.getDeclaredConstructors()[0].getParameterCount()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("UserService 통합 시나리오 테스트")
  class UserServiceIntegrationTest {

    @Test
    @DisplayName("사용자 등록 후 조회가 정상적으로 동작한다")
    void shouldRegisterAndFetchUserSuccessfully() {
      // given
      String email = "integration@test.com";
      UserRegisterCommand registerCommand =
          UserRegisterCommand.builder()
              .username("integrationuser")
              .encryptedPassword("encrypted-password")
              .email(email)
              .phone("010-1234-5678")
              .build();

      // 등록 시에는 사용자가 존재하지 않음
      given(fetchUserPort.findByEmail(email)).willReturn(Optional.empty());

      UserPortResponse createdUser =
          UserPortResponse.builder()
              .userId("integration-user-id")
              .username("integrationuser")
              .password("encrypted-password")
              .email(email)
              .phone("010-1234-5678")
              .role("USER")
              .build();

      given(insertUserPort.create(any(CreateUser.class))).willReturn(createdUser);

      // when - 사용자 등록
      UserRegisterResponse registerResult = userService.register(registerCommand);

      // then - 등록 결과 검증
      assertThat(registerResult.username()).isEqualTo("integrationuser");
      assertThat(registerResult.email()).isEqualTo(email);

      // given - 조회 시에는 사용자가 존재함
      given(fetchUserPort.findByEmail(email)).willReturn(Optional.of(createdUser));

      // when - 사용자 조회
      UserResponse fetchResult = userService.fetchUserByEmail(email);

      // then - 조회 결과 검증
      assertThat(fetchResult.username()).isEqualTo("integrationuser");
      assertThat(fetchResult.email()).isEqualTo(email);
      assertThat(fetchResult.role()).isEqualTo("USER");
    }
  }
}
