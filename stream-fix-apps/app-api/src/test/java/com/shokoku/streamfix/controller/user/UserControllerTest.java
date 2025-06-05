package com.shokoku.streamfix.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shokoku.streamfix.advice.GlobalExceptionAdvice;
import com.shokoku.streamfix.controller.user.request.UserRegisterRequest;
import com.shokoku.streamfix.user.RegisterUserUseCase;
import com.shokoku.streamfix.user.response.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController API 테스트")
class UserControllerTest {

  @Mock private RegisterUserUseCase registerUserUseCase;
  @Mock private AuthenticationManagerBuilder authenticationManagerBuilder;

  @InjectMocks private UserController userController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionAdvice())
            .build();
  }

  @Nested
  @DisplayName("POST /api/v1/user/register API는")
  class DescribeUserRegister {

    @Nested
    @DisplayName("올바른 사용자 정보로 요청 시")
    class ContextWithValidUserInfo {

      @Test
      @DisplayName("성공(200 OK) 상태 코드와 사용자 등록 응답을 반환한다")
      void it_returns_200_ok_and_register_response() throws Exception {
        // given
        UserRegisterRequest request =
            new UserRegisterRequest("testuser", "password123", "test@example.com", "010-1234-5678");

        UserRegisterResponse mockResponse =
            new UserRegisterResponse("testuser", "test@example.com", "010-1234-5678");

        given(registerUserUseCase.register(any())).willReturn(mockResponse);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                post("/api/v1/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJsonContent(request)));

        // then
        resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.username", is("testuser")))
            .andExpect(jsonPath("$.data.email", is("test@example.com")))
            .andExpect(jsonPath("$.data.phone", is("010-1234-5678")));

        verify(registerUserUseCase, times(1)).register(any());
      }

      @Test
      @DisplayName("한글 사용자명도 정상적으로 처리한다")
      void it_handles_korean_username() throws Exception {
        // given
        UserRegisterRequest request =
            new UserRegisterRequest("한글사용자", "password123", "korean@example.com", "010-9876-5432");

        UserRegisterResponse mockResponse =
            new UserRegisterResponse("한글사용자", "korean@example.com", "010-9876-5432");

        given(registerUserUseCase.register(any())).willReturn(mockResponse);

        // when
        ResultActions resultActions =
            mockMvc.perform(
                post("/api/v1/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJsonContent(request)));

        // then
        resultActions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.username", is("한글사용자")));
      }
    }

    @Nested
    @DisplayName("UseCase에서 예외 발생 시")
    class ContextWithUseCaseException {

      @Test
      @DisplayName("비즈니스 예외 발생 시 실패 응답을 반환한다")
      void it_returns_failure_response_when_business_exception() throws Exception {
        // given
        UserRegisterRequest request =
            new UserRegisterRequest(
                "testuser", "password123", "existing@example.com", "010-1234-5678");

        given(registerUserUseCase.register(any()))
            .willThrow(new RuntimeException("User already exists"));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                post("/api/v1/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJsonContent(request)));

        // then
        resultActions
            .andDo(print())
            .andExpect(status().isOk()) // GlobalExceptionAdvice는 200 반환
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is("User already exists")));
      }
    }
  }

  @Nested
  @DisplayName("PasswordEncryption 어노테이션 테스트")
  class PasswordEncryptionTest {

    @Test
    @DisplayName("UserRegisterRequest의 password 필드에 @PasswordEncryption 어노테이션이 적용되어 있다")
    void password_field_should_have_password_encryption_annotation() throws Exception {
      // given
      Class<UserRegisterRequest> requestClass = UserRegisterRequest.class;

      // when
      var passwordField = requestClass.getDeclaredField("password");

      // then
      assertThat(
              passwordField.isAnnotationPresent(
                  com.shokoku.streamfix.annotaion.PasswordEncryption.class))
          .isTrue();
    }
  }

  // 헬퍼 메서드
  private String createJsonContent(Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("JSON 변환 실패", e);
    }
  }
}
