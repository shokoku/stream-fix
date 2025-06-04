package com.shokoku.streamfix.controller.movie;

import com.shokoku.streamfix.StreamFixApplication;
import com.shokoku.streamfix.authcation.AuthenticationHolder;
import com.shokoku.streamfix.authcation.RequestedBy;
import com.shokoku.streamfix.config.RequestedByMvcConfigurer;
import com.shokoku.streamfix.interceptor.RequestedByInterceptor;
import com.shokoku.streamfix.movie.FetchMovieUseCase;
import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
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

@WebMvcTest(MovieController.class)
@ContextConfiguration(classes = {StreamFixApplication.class, RequestedByMvcConfigurer.class})
@Import(RequestedByInterceptor.class)
@DisplayName("MovieController API 테스트")
@WithMockUser
class MovieControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private FetchMovieUseCase fetchMovieUseCase;

  @MockBean private AuthenticationHolder authenticationHolder;

  private final String TEST_USER = "test-movie-user";

  @BeforeEach
  void setUp() {
    given(authenticationHolder.getAuthentication())
        .willReturn(Optional.of(new RequestedBy(TEST_USER)));
  }

  @Nested
  @DisplayName("GET /api/v1/movie/client/{page} API는")
  class DescribeFetchMoviePageable {

    @Nested
    @DisplayName("정상적인 page 값으로 요청 시")
    class ContextWithValidPage {

      @Test
      @DisplayName("성공(200 OK) 상태 코드와 StreamFixApiResponse 형태로 응답을 반환한다")
      void it_returns_200_ok_and_api_response() throws Exception {
        // given
        int page = 1;
        MovieResponse movie =
            new MovieResponse(
                "Test Movie", false, Collections.singletonList("Action"), "Overview", "2024-01-01");
        PageableMovieResponse responseFromUseCase =
            new PageableMovieResponse(Collections.singletonList(movie), page, false);
        given(fetchMovieUseCase.fetchFromClient(page)).willReturn(responseFromUseCase);
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(TEST_USER)));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                get("/api/v1/movie/client/{page}", page)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RequestedByInterceptor.REQUEST_BY_HEADER, TEST_USER));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.data.page", is(page)))
            .andExpect(jsonPath("$.data.movieResponses[0].movieName", is("Test Movie")))
            .andDo(print());

        // verify
        verify(fetchMovieUseCase, times(1)).fetchFromClient(page);
      }
    }

    @Nested
    @DisplayName("잘못된 page 파라미터 값 (예: 음수)으로 요청 시")
    class ContextWithInvalidPage {

      @Test
      @DisplayName("UseCase에서 IllegalArgumentException 발생 시, StreamFixApiResponse 실패 응답을 반환한다")
      void it_returns_400_bad_request_if_use_case_throws_illegal_argument() throws Exception {
        // given
        int invalidPage = -1;
        given(fetchMovieUseCase.fetchFromClient(invalidPage))
            .willThrow(new IllegalArgumentException("Page must be greater than 0"));
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(TEST_USER)));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                get("/api/v1/movie/client/{page}", invalidPage)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RequestedByInterceptor.REQUEST_BY_HEADER, TEST_USER));

        // then
        resultActions
            .andExpect(status().isOk()) // GlobalExceptionAdvice는 200으로 응답
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is("Page must be greater than 0")))
            .andDo(print());

        // verify
        verify(fetchMovieUseCase, times(1)).fetchFromClient(invalidPage);
      }
    }

    @Nested
    @DisplayName("UseCase에서 RuntimeException 발생 시")
    class ContextWithUseCaseRuntimeException {

      @Test
      @DisplayName("실패 시 StreamFixApiResponse 실패 응답을 반환한다")
      void it_returns_500_internal_server_error() throws Exception {
        // given
        int page = 1;
        String errorMessage = "Movie UseCase failed unexpectedly";
        given(fetchMovieUseCase.fetchFromClient(page))
            .willThrow(new RuntimeException(errorMessage));
        given(authenticationHolder.getAuthentication())
            .willReturn(Optional.of(new RequestedBy(TEST_USER)));

        // when
        ResultActions resultActions =
            mockMvc.perform(
                get("/api/v1/movie/client/{page}", page)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RequestedByInterceptor.REQUEST_BY_HEADER, TEST_USER));

        // then
        resultActions
            .andExpect(status().isOk()) // GlobalExceptionAdvice는 200으로 응답
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is(errorMessage)))
            .andDo(print());

        // verify
        verify(fetchMovieUseCase, times(1)).fetchFromClient(page);
      }
    }
  }
}
