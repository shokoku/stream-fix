package com.shokoku.streamfix.sample;

import com.shokoku.streamfix.StreamFixApplication;
import com.shokoku.streamfix.authcation.AuthenticationHolder;
import com.shokoku.streamfix.authcation.RequestedBy;
import com.shokoku.streamfix.config.RequestedByMvcConfigurer;
import com.shokoku.streamfix.interceptor.RequestedByInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SampleController.class)
@ContextConfiguration(classes = {StreamFixApplication.class, RequestedByMvcConfigurer.class})
@Import(RequestedByInterceptor.class)
@DisplayName("SampleController API 테스트")
class SampleControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SearchSampleUseCase searchSampleUseCase;

  @MockBean // 인터셉터 의존성 Mock 처리
  private AuthenticationHolder authenticationHolder;

  private final String TEST_USER = "test-user";
  private final String DEFAULT_SAMPLE_NAME = "Default Sample Name";

  @BeforeEach
  void setUp() {
    // 모든 테스트 전에 공통적으로 AuthenticationHolder Mock 설정
    // 각 테스트에서 필요에 따라 override 가능
    given(authenticationHolder.getAuthentication())
        .willReturn(Optional.of(new RequestedBy(TEST_USER)));
  }

  @Nested
  @DisplayName("GET /api/v1/sample API는")
  class DescribeGetSample {

    @Nested
    @DisplayName("정상적인 요청 시")
    class ContextWithValidRequest {

      @Test
      @DisplayName("성공(200 OK) 상태 코드와 샘플 응답을 반환한다")
      void it_returns_200_ok_and_sample_response() throws Exception {
        // given
        String expectedName = "Test Sample";
        SampleResponse mockResponse = new SampleResponse(expectedName);
        given(searchSampleUseCase.getSample()).willReturn(mockResponse);
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(TEST_USER)));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                get("/api/v1/sample")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RequestedByInterceptor.REQUEST_BY_HEADER, TEST_USER));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(expectedName)))
            .andDo(print()); // 요청/응답 상세 로깅

        // verify
        verify(searchSampleUseCase, times(1)).getSample();
      }
    }

    @Nested
    @DisplayName("Request-By 헤더가 없는 요청 시")
    class ContextWithMissingRequestByHeader {
      @Test
      @DisplayName("인터셉터의 기본값으로 처리되어 성공(200 OK)하고 기본 샘플 응답을 반환할 수 있다")
      void it_handles_with_interceptor_default_and_returns_200_ok() throws Exception {
        // given
        SampleResponse mockResponse = new SampleResponse(DEFAULT_SAMPLE_NAME);
        given(searchSampleUseCase.getSample()).willReturn(mockResponse);
        // 헤더가 없을 때 인터셉터가 RequestedBy(null)을 반환하도록 AuthenticationHolder Mock 설정
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(null)));

        // when
        ResultActions resultActions =
            mockMvc.perform(get("/api/v1/sample").contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is(DEFAULT_SAMPLE_NAME)))
            .andDo(print());

        // verify
        verify(searchSampleUseCase, times(1)).getSample();
      }
    }

    @Nested
    @DisplayName("UseCase에서 예외 발생 시")
    class ContextWithUseCaseException {

      @Test
      @DisplayName("실패(500 Internal Server Error) 상태 코드와 에러 정보를 반환한다")
      void it_returns_500_internal_server_error() throws Exception {
        // given
        String errorMessage = "UseCase failed unexpectedly";
        given(searchSampleUseCase.getSample()).willThrow(new RuntimeException(errorMessage));
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(TEST_USER)));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                get("/api/v1/sample")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RequestedByInterceptor.REQUEST_BY_HEADER, TEST_USER));

        // then
        resultActions
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(500)))
            .andExpect(jsonPath("$.error", is("Internal Server Error")))
            .andExpect(jsonPath("$.message", is(errorMessage)))
            .andExpect(jsonPath("$.path", is("/api/v1/sample")))
            .andDo(print());

        // verify
        verify(searchSampleUseCase, times(1)).getSample();
      }
    }
  }
}
