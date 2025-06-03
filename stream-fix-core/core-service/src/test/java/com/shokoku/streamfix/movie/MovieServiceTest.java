package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("MovieService 영화 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

  @Mock private TmdbMoviePort tmdbMoviePort;

  @InjectMocks private MovieService movieService;

  @Nested
  @DisplayName("fetchFromClient 정상 시나리오 테스트")
  class FetchFromClientNormalScenarioTest {

    @Test
    @DisplayName("TMDB에서 영화 데이터를 가져와 응답 형태로 변환한다")
    void shouldFetchMoviesFromTmdbAndTransformToResponse() {
      // given
      int requestedPage = 1;
      List<TmdbMovie> tmdbMovies = createSampleTmdbMovies();
      TmdbPageableMovies tmdbPageableMovies =
          new TmdbPageableMovies(tmdbMovies, requestedPage, true);

      given(tmdbMoviePort.fetchPageable(requestedPage)).willReturn(tmdbPageableMovies);

      // when
      PageableMovieResponse result = movieService.fetchFromClient(requestedPage);

      // then
      assertThat(result).isNotNull();
      assertThat(result.page()).isEqualTo(requestedPage);
      assertThat(result.hasNext()).isTrue();
      assertThat(result.movieResponses()).hasSize(3);

      // 변환된 데이터 검증
      MovieResponse firstMovie = result.movieResponses().get(0);
      assertThat(firstMovie.movieName()).isEqualTo("어벤져스: 엔드게임");
      assertThat(firstMovie.isAdult()).isFalse();
      assertThat(firstMovie.genre()).containsExactly("액션", "어드벤처", "SF");

      // 포트 호출 검증
      then(tmdbMoviePort).should(times(1)).fetchPageable(requestedPage);
    }

    @Test
    @DisplayName("빈 영화 목록도 올바르게 처리한다")
    void shouldHandleEmptyMovieList() {
      // given
      int requestedPage = 10;
      TmdbPageableMovies emptyPageableMovies =
          new TmdbPageableMovies(Collections.emptyList(), requestedPage, false);

      given(tmdbMoviePort.fetchPageable(requestedPage)).willReturn(emptyPageableMovies);

      // when
      PageableMovieResponse result = movieService.fetchFromClient(requestedPage);

      // then
      assertThat(result).isNotNull();
      assertThat(result.movieResponses()).isEmpty();
      assertThat(result.page()).isEqualTo(requestedPage);
      assertThat(result.hasNext()).isFalse();

      then(tmdbMoviePort).should().fetchPageable(requestedPage);
    }

    @ParameterizedTest(name = "페이지 {0}에 대해 올바른 응답을 반환한다")
    @ValueSource(ints = {1, 2, 5, 10, 50})
    @DisplayName("다양한 페이지 번호에 대해 올바른 응답을 반환한다")
    void shouldReturnCorrectResponseForVariousPages(int page) {
      // given
      TmdbPageableMovies pageableMovies =
          new TmdbPageableMovies(createSampleTmdbMovies(), page, page < 10);
      given(tmdbMoviePort.fetchPageable(page)).willReturn(pageableMovies);

      // when
      PageableMovieResponse result = movieService.fetchFromClient(page);

      // then
      assertThat(result.page()).isEqualTo(page);
      assertThat(result.hasNext()).isEqualTo(page < 10);
      then(tmdbMoviePort).should().fetchPageable(page);
    }
  }

  @Nested
  @DisplayName("fetchFromClient 예외 상황 테스트")
  class FetchFromClientExceptionTest {

    @Test
    @DisplayName("TMDB 포트에서 예외 발생 시 그대로 전파한다")
    void shouldPropagateExceptionFromTmdbPort() {
      // given
      int page = 1;
      given(tmdbMoviePort.fetchPageable(page)).willThrow(new RuntimeException("TMDB API 연결 실패"));

      // when & then
      assertThatThrownBy(() -> movieService.fetchFromClient(page))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("TMDB API 연결 실패");

      then(tmdbMoviePort).should().fetchPageable(page);
    }

    @Test
    @DisplayName("TMDB 포트가 null을 반환할 때 NullPointerException이 발생한다")
    void shouldThrowNullPointerExceptionWhenTmdbPortReturnsNull() {
      // given
      int page = 1;
      given(tmdbMoviePort.fetchPageable(page)).willReturn(null);

      // when & then
      assertThatThrownBy(() -> movieService.fetchFromClient(page))
          .isInstanceOf(NullPointerException.class);

      then(tmdbMoviePort).should().fetchPageable(page);
    }
  }

  @Nested
  @DisplayName("MovieService 인터페이스 구현 테스트")
  class MovieServiceInterfaceTest {

    @Test
    @DisplayName("FetchMovieUseCase 인터페이스를 올바르게 구현한다")
    void shouldImplementFetchMovieUseCaseCorrectly() {
      // given
      TmdbPageableMovies pageableMovies =
          new TmdbPageableMovies(createSampleTmdbMovies(), 1, false);
      given(tmdbMoviePort.fetchPageable(1)).willReturn(pageableMovies);

      // when & then
      assertThat(movieService).isInstanceOf(FetchMovieUseCase.class);

      PageableMovieResponse result = movieService.fetchFromClient(1);
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("서비스 클래스가 필요한 어노테이션들을 가진다")
    void shouldHaveRequiredAnnotations() {
      // given
      Class<MovieService> serviceClass = MovieService.class;

      // when & then
      assertThat(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class))
          .isTrue();

      // Lombok 어노테이션은 컴파일 시에 처리되므로 생성자 확인으로 대체
      assertThat(serviceClass.getDeclaredConstructors()).hasSize(1);
      assertThat(serviceClass.getDeclaredConstructors()[0].getParameterCount()).isEqualTo(1);
    }
  }

  // 테스트 헬퍼 메서드들
  private List<TmdbMovie> createSampleTmdbMovies() {
    return Arrays.asList(
        new TmdbMovie(
            "어벤져스: 엔드게임",
            false,
            Arrays.asList("액션", "어드벤처", "SF"),
            "마블 시네마틱 유니버스의 대미",
            "2019-04-25"),
        new TmdbMovie(
            "기생충", false, Arrays.asList("스릴러", "드라마", "코미디"), "기택네 가족의 기생 이야기", "2019-05-30"),
        new TmdbMovie(
            "인터스텔라", false, Arrays.asList("SF", "드라마", "모험"), "우주를 향한 인류의 도전", "2014-11-06"));
  }
}
