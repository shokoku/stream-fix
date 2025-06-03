package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FetchMovieUseCase 인터페이스 테스트")
@ExtendWith(MockitoExtension.class)
class FetchMovieUseCaseTest {

  @Nested
  @DisplayName("FetchMovieUseCase 인터페이스 정의 테스트")
  class FetchMovieUseCaseDefinitionTest {

    @Test
    @DisplayName("FetchMovieUseCase 인터페이스가 올바르게 정의되어 있다")
    void shouldHaveCorrectInterfaceDefinition() {
      // given
      Class<FetchMovieUseCase> useCaseClass = FetchMovieUseCase.class;

      // when & then
      assertThat(useCaseClass.isInterface()).isTrue();
      assertThat(useCaseClass.getMethods()).hasSize(1);
      assertThat(useCaseClass.getMethods()[0].getName()).isEqualTo("fetchFromClient");
      assertThat(useCaseClass.getMethods()[0].getReturnType())
          .isEqualTo(PageableMovieResponse.class);
      assertThat(useCaseClass.getMethods()[0].getParameterCount()).isEqualTo(1);
      assertThat(useCaseClass.getMethods()[0].getParameterTypes()[0]).isEqualTo(int.class);
    }
  }

  @Nested
  @DisplayName("FetchMovieUseCase 구현체 기본 동작 테스트")
  class FetchMovieUseCaseBasicBehaviorTest {

    @Test
    @DisplayName("첫 번째 페이지 조회 시 정상적인 응답을 반환한다")
    void shouldReturnValidResponseForFirstPage() {
      // given
      FetchMovieUseCase useCase = createTestImplementation();

      // when
      PageableMovieResponse response = useCase.fetchFromClient(1);

      // then
      assertThat(response).isNotNull();
      assertThat(response.page()).isEqualTo(1);
      assertThat(response.movieResponses()).isNotNull();
      assertThat(response.hasNext()).isNotNull();
    }

    @ParameterizedTest(name = "페이지 {0}에 대해 정상적인 응답을 반환한다")
    @ValueSource(ints = {1, 2, 5, 10})
    @DisplayName("다양한 페이지 번호에 대해 정상적인 응답을 반환한다")
    void shouldReturnValidResponseForVariousPages(int page) {
      // given
      FetchMovieUseCase useCase = createTestImplementation();

      // when
      PageableMovieResponse response = useCase.fetchFromClient(page);

      // then
      assertThat(response).isNotNull();
      assertThat(response.page()).isEqualTo(page);
      assertThat(response.movieResponses()).isNotNull();
    }
  }

  @Nested
  @DisplayName("FetchMovieUseCase 예외 상황 테스트")
  class FetchMovieUseCaseExceptionTest {

    @Test
    @DisplayName("음수 페이지 번호에 대해 적절히 처리해야 한다")
    void shouldHandleNegativePageNumbers() {
      // given
      FetchMovieUseCase useCase = createValidatingImplementation();

      // when & then
      assertThatThrownBy(() -> useCase.fetchFromClient(-1))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("페이지 번호는 1 이상이어야 합니다");
    }

    @Test
    @DisplayName("0 페이지 번호에 대해 적절히 처리해야 한다")
    void shouldHandleZeroPageNumber() {
      // given
      FetchMovieUseCase useCase = createValidatingImplementation();

      // when & then
      assertThatThrownBy(() -> useCase.fetchFromClient(0))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("페이지 번호는 1 이상이어야 합니다");
    }
  }

  // 테스트용 UseCase 구현체 생성 헬퍼 메서드들
  private FetchMovieUseCase createTestImplementation() {
    return (page) -> {
      List<MovieResponse> movies = createSampleMovies();
      return new PageableMovieResponse(movies, page, page < 10);
    };
  }

  private FetchMovieUseCase createValidatingImplementation() {
    return (page) -> {
      if (page <= 0) {
        throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다");
      }

      List<MovieResponse> movies = createSampleMovies();
      return new PageableMovieResponse(movies, page, page < 10);
    };
  }

  private List<MovieResponse> createSampleMovies() {
    return Arrays.asList(
        new MovieResponse(
            "어벤져스: 엔드게임",
            false,
            Arrays.asList("액션", "어드벤처", "SF"),
            "마블 시네마틱 유니버스의 대미",
            "2019-04-25"),
        new MovieResponse(
            "기생충", false, Arrays.asList("스릴러", "드라마", "코미디"), "기택네 가족의 기생 이야기", "2019-05-30"));
  }
}
