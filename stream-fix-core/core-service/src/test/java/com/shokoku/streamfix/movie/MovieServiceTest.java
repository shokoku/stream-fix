package com.shokoku.streamfix.movie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
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
class MovieServiceTest {

  @InjectMocks MovieService sut;

  @Mock TmdbMoviePort tmdbMoviePort;
  @Mock PersistenceMoviePort persistenceMoviePort;
  @Mock DownloadMoviePort downloadMoviePort;
  @Mock LikeMoviePort likeMoviePort;
  @Mock List<UserDownloadMovieRoleValidator> validators;

  @Nested
  @DisplayName("fetchFromClient: 외부 클라이언트에서 영화 조회")
  class FetchFromClient {

    @DisplayName("실패: page가 음수이면 포트에서 예외가 발생할 수 있다")
    @Test
    void test1() {
      // given
      int invalidPage = -1;
      when(tmdbMoviePort.fetchPageable(invalidPage))
          .thenThrow(new RuntimeException("Invalid page number"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchFromClient(invalidPage));
      verify(tmdbMoviePort).fetchPageable(invalidPage);
    }

    @DisplayName("실패: 외부 API 호출 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      int page = 1;
      when(tmdbMoviePort.fetchPageable(page)).thenThrow(new RuntimeException("TMDB API 서버 오류"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchFromClient(page));
      verify(tmdbMoviePort).fetchPageable(page);
    }

    @DisplayName("성공: 외부 클라이언트에서 영화 목록을 성공적으로 가져온다")
    @Test
    void test1000() {
      // given
      int page = 1;
      TmdbMovie tmdbMovie = mock(TmdbMovie.class);
      when(tmdbMovie.movieName()).thenReturn("Test Movie");
      when(tmdbMovie.isAdult()).thenReturn(false);
      when(tmdbMovie.genre()).thenReturn(List.of("Action", "Drama"));
      when(tmdbMovie.overview()).thenReturn("Test overview");
      when(tmdbMovie.releaseAt()).thenReturn("2024-01-01");

      TmdbPageableMovies tmdbPageableMovies = mock(TmdbPageableMovies.class);
      when(tmdbPageableMovies.tmdbMovies()).thenReturn(List.of(tmdbMovie));
      when(tmdbPageableMovies.page()).thenReturn(page);
      when(tmdbPageableMovies.hasNext()).thenReturn(true);

      when(tmdbMoviePort.fetchPageable(page)).thenReturn(tmdbPageableMovies);

      // when
      PageableMovieResponse result = sut.fetchFromClient(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      assertTrue(result.hasNext());
      assertEquals(1, result.movieResponses().size());

      MovieResponse movieResponse = result.movieResponses().get(0);
      assertEquals("Test Movie", movieResponse.movieName());
      assertEquals(false, movieResponse.isAdult());
      assertEquals(List.of("Action", "Drama"), movieResponse.genre());
      assertEquals("Test overview", movieResponse.overview());
      assertEquals("2024-01-01", movieResponse.releaseAt());

      verify(tmdbMoviePort).fetchPageable(page);
    }

    @DisplayName("성공: 빈 영화 목록도 정상적으로 반환한다")
    @Test
    void test1001() {
      // given
      int page = 1;
      TmdbPageableMovies emptyPageableMovies = mock(TmdbPageableMovies.class);
      when(emptyPageableMovies.tmdbMovies()).thenReturn(Collections.emptyList());
      when(emptyPageableMovies.page()).thenReturn(page);
      when(emptyPageableMovies.hasNext()).thenReturn(false);

      when(tmdbMoviePort.fetchPageable(page)).thenReturn(emptyPageableMovies);

      // when
      PageableMovieResponse result = sut.fetchFromClient(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      assertFalse(result.hasNext());
      assertTrue(result.movieResponses().isEmpty());
    }

    @DisplayName("성공: 다양한 페이지를 조회할 수 있다")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 100})
    void test1002(int page) {
      // given
      TmdbPageableMovies pageableMovies = mock(TmdbPageableMovies.class);
      when(pageableMovies.tmdbMovies()).thenReturn(Collections.emptyList());
      when(pageableMovies.page()).thenReturn(page);
      when(pageableMovies.hasNext()).thenReturn(page < 10);

      when(tmdbMoviePort.fetchPageable(page)).thenReturn(pageableMovies);

      // when
      PageableMovieResponse result = sut.fetchFromClient(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      assertEquals(page < 10, result.hasNext());
    }
  }

  @Nested
  @DisplayName("fetchFromDb: 데이터베이스에서 영화 조회")
  class FetchFromDb {

    @DisplayName("실패: page가 음수이면 포트에서 예외가 발생할 수 있다")
    @Test
    void test1() {
      // given
      int invalidPage = -1;
      when(persistenceMoviePort.fetchBy(invalidPage, 10))
          .thenThrow(new RuntimeException("Invalid page number"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchFromDb(invalidPage));
      verify(persistenceMoviePort).fetchBy(invalidPage, 10);
    }

    @DisplayName("실패: DB 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      int page = 1;
      when(persistenceMoviePort.fetchBy(page, 10))
          .thenThrow(new RuntimeException("Database connection failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchFromDb(page));
      verify(persistenceMoviePort).fetchBy(page, 10);
    }

    @DisplayName("성공: 데이터베이스에서 영화 목록을 성공적으로 가져온다")
    @Test
    void test1000() {
      // given
      int page = 1;
      StreamFixMovie streamFixMovie = mock(StreamFixMovie.class);
      when(streamFixMovie.movieName()).thenReturn("DB Movie");
      when(streamFixMovie.isAdult()).thenReturn(true);
      when(streamFixMovie.overview()).thenReturn("DB overview");
      when(streamFixMovie.releasedAt()).thenReturn("2024-02-01");

      when(persistenceMoviePort.fetchBy(page, 10)).thenReturn(List.of(streamFixMovie));

      // when
      PageableMovieResponse result = sut.fetchFromDb(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      assertTrue(result.hasNext()); // 현재 구현에서는 항상 true
      assertEquals(1, result.movieResponses().size());

      MovieResponse movieResponse = result.movieResponses().get(0);
      assertEquals("DB Movie", movieResponse.movieName());
      assertEquals(true, movieResponse.isAdult());
      assertEquals(List.of(), movieResponse.genre()); // DB에서는 빈 장르 리스트
      assertEquals("DB overview", movieResponse.overview());
      assertEquals("2024-02-01", movieResponse.releaseAt());

      verify(persistenceMoviePort).fetchBy(page, 10);
    }

    @DisplayName("성공: 빈 영화 목록도 정상적으로 반환한다")
    @Test
    void test1001() {
      // given
      int page = 1;
      when(persistenceMoviePort.fetchBy(page, 10)).thenReturn(Collections.emptyList());

      // when
      PageableMovieResponse result = sut.fetchFromDb(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      assertTrue(result.hasNext()); // 현재 구현에서는 항상 true
      assertTrue(result.movieResponses().isEmpty());
    }

    @DisplayName("성공: 다양한 페이지를 조회할 수 있다")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void test1002(int page) {
      // given
      when(persistenceMoviePort.fetchBy(page, 10)).thenReturn(Collections.emptyList());

      // when
      PageableMovieResponse result = sut.fetchFromDb(page);

      // then
      assertNotNull(result);
      assertEquals(page, result.page());
      verify(persistenceMoviePort).fetchBy(page, 10);
    }
  }

  @Nested
  @DisplayName("insert: 영화 목록 저장")
  class Insert {

    @DisplayName("실패: null 목록이 전달되면 NullPointerException을 던진다")
    @Test
    void test1() {
      // when & then
      assertThrows(NullPointerException.class, () -> sut.insert(null));
      verify(persistenceMoviePort, never()).insert(any(StreamFixMovie.class));
    }

    @DisplayName("실패: 영화 저장 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      MovieResponse movieResponse =
          new MovieResponse("Error Movie", false, List.of("Drama"), "Error overview", "2024-01-01");
      List<MovieResponse> movies = List.of(movieResponse);

      doThrow(new RuntimeException("Database insertion failed"))
          .when(persistenceMoviePort)
          .insert(any(StreamFixMovie.class));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.insert(movies));
      verify(persistenceMoviePort).insert(any(StreamFixMovie.class));
    }

    @DisplayName("성공: 빈 목록도 정상적으로 처리한다")
    @Test
    void test1000() {
      // given
      List<MovieResponse> emptyMovies = Collections.emptyList();

      // when
      sut.insert(emptyMovies);

      // then
      verify(persistenceMoviePort, never()).insert(any(StreamFixMovie.class));
    }

    @DisplayName("성공: 영화 목록을 성공적으로 저장한다")
    @Test
    void test1001() {
      // given
      MovieResponse movieResponse =
          new MovieResponse(
              "Insert Movie", false, List.of("Drama"), "Insert overview", "2024-01-01");
      List<MovieResponse> movies = List.of(movieResponse);

      // when
      sut.insert(movies);

      // then
      verify(persistenceMoviePort).insert(any(StreamFixMovie.class));
    }

    @DisplayName("성공: 여러 영화를 동시에 저장할 수 있다")
    @Test
    void test1002() {
      // given
      MovieResponse movie1 =
          new MovieResponse("Movie 1", false, List.of("Action"), "Overview 1", "2024-01-01");
      MovieResponse movie2 =
          new MovieResponse("Movie 2", true, List.of("Horror"), "Overview 2", "2024-02-01");
      List<MovieResponse> movies = List.of(movie1, movie2);

      // when
      sut.insert(movies);

      // then
      verify(persistenceMoviePort, times(2)).insert(any(StreamFixMovie.class));
    }

    @DisplayName("성공: 다양한 영화 데이터를 처리할 수 있다")
    @Test
    void test1003() {
      // given
      MovieResponse adultMovie =
          new MovieResponse("Adult Movie", true, List.of("Adult"), "Adult overview", "2024-01-01");
      MovieResponse nonAdultMovie =
          new MovieResponse(
              "Family Movie", false, List.of("Family"), "Family overview", "2024-01-01");
      List<MovieResponse> movies = List.of(adultMovie, nonAdultMovie);

      // when
      sut.insert(movies);

      // then
      verify(persistenceMoviePort, times(2)).insert(any(StreamFixMovie.class));
    }
  }

  @Nested
  @DisplayName("download: 영화 다운로드")
  class Download {
    final String userId = "user123";
    final String movieId = "movie123";
    final String role = "ROLE_BRONZE";

    @DisplayName("실패: userId가 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidUserId) {
      // given
      when(downloadMoviePort.downloadCntToday(invalidUserId))
          .thenThrow(new IllegalArgumentException("Invalid userId"));

      // when & then
      assertThrows(
          IllegalArgumentException.class, () -> sut.download(invalidUserId, role, movieId));
    }

    @DisplayName("실패: 해당 role의 validator가 없으면 NoSuchElementException을 던진다")
    @Test
    void test2() {
      // given
      String unknownRole = "ROLE_UNKNOWN";
      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(0L);
      when(validators.stream()).thenReturn(Stream.empty());

      // when & then
      assertThrows(NoSuchElementException.class, () -> sut.download(userId, unknownRole, movieId));
      verify(downloadMoviePort).downloadCntToday(userId);
    }

    @DisplayName("실패: 다운로드 횟수 제한에 걸리면 RuntimeException을 던진다")
    @Test
    void test3() {
      // given
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(role)).thenReturn(true);
      when(validator.validate(5L)).thenReturn(false);

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(5L);
      when(validators.stream()).thenReturn(Stream.of(validator));

      // when & then
      RuntimeException exception =
          assertThrows(RuntimeException.class, () -> sut.download(userId, role, movieId));
      assertEquals("더 이상 다운로드를 할 수 없습니다.", exception.getMessage());

      verify(downloadMoviePort).downloadCntToday(userId);
      verify(validator).isTarget(role);
      verify(validator).validate(5L);
      verify(persistenceMoviePort, never()).findBy(movieId);
    }

    @DisplayName("실패: 존재하지 않는 영화를 다운로드하면 관련 Exception을 던진다")
    @Test
    void test4() {
      // given
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(role)).thenReturn(true);
      when(validator.validate(0L)).thenReturn(true);

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(0L);
      when(validators.stream()).thenReturn(Stream.of(validator));
      when(persistenceMoviePort.findBy(movieId)).thenThrow(new RuntimeException("Movie not found"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.download(userId, role, movieId));
      verify(persistenceMoviePort).findBy(movieId);
    }

    @DisplayName("실패: 다운로드 기록 저장 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test5() {
      // given
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(role)).thenReturn(true);
      when(validator.validate(0L)).thenReturn(true);

      StreamFixMovie movie = mock(StreamFixMovie.class);

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(0L);
      when(validators.stream()).thenReturn(Stream.of(validator));
      when(persistenceMoviePort.findBy(movieId)).thenReturn(movie);
      doThrow(new RuntimeException("Save failed"))
          .when(downloadMoviePort)
          .save(any(UserMovieDownload.class));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.download(userId, role, movieId));
      verify(downloadMoviePort).save(any(UserMovieDownload.class));
    }

    @DisplayName("성공: BRONZE 역할로 영화를 성공적으로 다운로드한다")
    @Test
    void test1000() {
      // given
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(role)).thenReturn(true);
      when(validator.validate(2L)).thenReturn(true); // BRONZE는 5개 미만

      StreamFixMovie movie = mock(StreamFixMovie.class);
      when(movie.movieName()).thenReturn("Test Movie");

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(2L);
      when(validators.stream()).thenReturn(Stream.of(validator));
      when(persistenceMoviePort.findBy(movieId)).thenReturn(movie);

      // when
      String result = sut.download(userId, role, movieId);

      // then
      assertEquals("Test Movie", result);
      verify(downloadMoviePort).downloadCntToday(userId);
      verify(validator).isTarget(role);
      verify(validator).validate(2L);
      verify(persistenceMoviePort).findBy(movieId);
      verify(downloadMoviePort).save(any(UserMovieDownload.class));
    }

    @DisplayName("성공: SILVER 역할로 영화를 성공적으로 다운로드한다")
    @Test
    void test1001() {
      // given
      String silverRole = "ROLE_SILVER";
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(silverRole)).thenReturn(true);
      when(validator.validate(8L)).thenReturn(true); // SILVER는 10개 미만

      StreamFixMovie movie = mock(StreamFixMovie.class);
      when(movie.movieName()).thenReturn("Silver Movie");

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(8L);
      when(validators.stream()).thenReturn(Stream.of(validator));
      when(persistenceMoviePort.findBy(movieId)).thenReturn(movie);

      // when
      String result = sut.download(userId, silverRole, movieId);

      // then
      assertEquals("Silver Movie", result);
      verify(validator).isTarget(silverRole);
      verify(validator).validate(8L);
    }

    @DisplayName("성공: GOLD 역할로 무제한 다운로드가 가능하다")
    @Test
    void test1002() {
      // given
      String goldRole = "ROLE_GOLD";
      UserDownloadMovieRoleValidator validator = mock(UserDownloadMovieRoleValidator.class);
      when(validator.isTarget(goldRole)).thenReturn(true);
      when(validator.validate(anyLong())).thenReturn(true); // GOLD는 무제한

      StreamFixMovie movie = mock(StreamFixMovie.class);
      when(movie.movieName()).thenReturn("Gold Movie");

      when(downloadMoviePort.downloadCntToday(userId)).thenReturn(100L); // 많은 다운로드 수
      when(validators.stream()).thenReturn(Stream.of(validator));
      when(persistenceMoviePort.findBy(movieId)).thenReturn(movie);

      // when
      String result = sut.download(userId, goldRole, movieId);

      // then
      assertEquals("Gold Movie", result);
      verify(validator).validate(100L);
    }
  }

  @Nested
  @DisplayName("like: 영화 좋아요")
  class Like {
    final String userId = "user123";
    final String movieId = "movie123";

    @DisplayName("실패: userId가 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidUserId) {
      // given
      when(likeMoviePort.findByUserIdAndMovieId(invalidUserId, movieId))
          .thenThrow(new IllegalArgumentException("Invalid userId"));

      // when & then
      assertThrows(IllegalArgumentException.class, () -> sut.like(invalidUserId, movieId));
    }

    @DisplayName("실패: movieId가 null이거나 빈 값이면 적절히 처리한다")
    @ParameterizedTest
    @NullAndEmptySource
    void test2(String invalidMovieId) {
      // given
      when(likeMoviePort.findByUserIdAndMovieId(userId, invalidMovieId))
          .thenThrow(new IllegalArgumentException("Invalid movieId"));

      // when & then
      assertThrows(IllegalArgumentException.class, () -> sut.like(userId, invalidMovieId));
    }

    @DisplayName("실패: 좋아요 조회 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test3() {
      // given
      when(likeMoviePort.findByUserIdAndMovieId(userId, movieId))
          .thenThrow(new RuntimeException("Database query failed"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.like(userId, movieId));
      verify(likeMoviePort).findByUserIdAndMovieId(userId, movieId);
    }

    @DisplayName("실패: 현재 구현 버그 - 처음 좋아요를 누르면 NoSuchElementException이 발생한다")
    @Test
    void test4() {
      // given - 현재 구현의 버그를 테스트
      when(likeMoviePort.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());

      // when & then
      // 현재 구현에서는 empty일 때도 get()을 호출하므로 예외가 발생함
      assertThrows(NoSuchElementException.class, () -> sut.like(userId, movieId));

      verify(likeMoviePort).findByUserIdAndMovieId(userId, movieId);
      verify(likeMoviePort).save(any(UserMovieLike.class)); // 새 좋아요는 저장됨
    }

    @DisplayName("실패: 좋아요 저장 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test5() {
      // given
      UserMovieLike existingLike = mock(UserMovieLike.class);
      when(likeMoviePort.findByUserIdAndMovieId(userId, movieId))
          .thenReturn(Optional.of(existingLike));
      doThrow(new RuntimeException("Database save failed")).when(likeMoviePort).save(existingLike);

      // when & then
      assertThrows(RuntimeException.class, () -> sut.like(userId, movieId));
      verify(existingLike).like();
      verify(likeMoviePort).save(existingLike);
    }

    @DisplayName("성공: 이미 좋아요가 있으면 좋아요 상태를 변경한다")
    @Test
    void test1000() {
      // given
      UserMovieLike existingLike = mock(UserMovieLike.class);
      when(likeMoviePort.findByUserIdAndMovieId(userId, movieId))
          .thenReturn(Optional.of(existingLike));

      // when
      sut.like(userId, movieId);

      // then
      verify(likeMoviePort).findByUserIdAndMovieId(userId, movieId);
      verify(existingLike).like(); // 좋아요 상태 변경
      verify(likeMoviePort).save(existingLike);
    }

    @DisplayName("성공: 다양한 사용자와 영화에 대한 좋아요를 처리할 수 있다")
    @Test
    void test1001() {
      // given
      String anotherUserId = "user456";
      String anotherMovieId = "movie456";
      UserMovieLike existingLike = mock(UserMovieLike.class);
      when(likeMoviePort.findByUserIdAndMovieId(anotherUserId, anotherMovieId))
          .thenReturn(Optional.of(existingLike));

      // when
      sut.like(anotherUserId, anotherMovieId);

      // then
      verify(likeMoviePort).findByUserIdAndMovieId(anotherUserId, anotherMovieId);
      verify(existingLike).like();
      verify(likeMoviePort).save(existingLike);
    }
  }
}
