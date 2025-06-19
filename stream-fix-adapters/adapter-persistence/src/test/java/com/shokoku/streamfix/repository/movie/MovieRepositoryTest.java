package com.shokoku.streamfix.repository.movie;

import static com.shokoku.streamfix.fixtures.MovieEntityFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.entity.movie.MovieEntity;
import com.shokoku.streamfix.movie.StreamFixMovie;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class MovieRepositoryTest {

  @InjectMocks MovieRepository sut;

  @Mock MovieJpaRepository movieJpaRepository;

  @Nested
  @DisplayName("fetchBy: 페이지네이션을 이용한 영화 조회")
  class FetchBy {

    @DisplayName("실패: 데이터베이스 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      int page = 0;
      int size = 10;
      when(movieJpaRepository.search(any(Pageable.class)))
          .thenThrow(new RuntimeException("Database connection error"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.fetchBy(page, size));
      verify(movieJpaRepository).search(PageRequest.of(page, size));
    }

    @DisplayName("성공: 빈 페이지를 반환한다")
    @Test
    void test1000() {
      // given
      int page = 0;
      int size = 10;
      Page<MovieEntity> emptyPage = new PageImpl<>(List.of());
      when(movieJpaRepository.search(any(Pageable.class))).thenReturn(emptyPage);

      // when
      List<StreamFixMovie> result = sut.fetchBy(page, size);

      // then
      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(movieJpaRepository).search(PageRequest.of(page, size));
    }

    @DisplayName("성공: 영화 목록을 페이지네이션으로 조회한다")
    @Test
    void test1001() {
      // given
      int page = 0;
      int size = 2;
      List<MovieEntity> movieEntities =
          List.of(aMovieEntity(), aMovieEntityWithName("Another Movie"));
      Page<MovieEntity> moviePage = new PageImpl<>(movieEntities);
      when(movieJpaRepository.search(any(Pageable.class))).thenReturn(moviePage);

      // when
      List<StreamFixMovie> result = sut.fetchBy(page, size);

      // then
      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals(DEFAULT_MOVIE_NAME, result.get(0).movieName());
      assertEquals("Another Movie", result.get(1).movieName());
      verify(movieJpaRepository).search(PageRequest.of(page, size));
    }
  }

  @Nested
  @DisplayName("findBy: 영화 이름으로 단일 영화 조회")
  class FindBy {

    @DisplayName("실패: 영화가 존재하지 않으면 예외를 던진다")
    @Test
    void test1() {
      // given
      String movieName = "Nonexistent Movie";
      when(movieJpaRepository.findByMovieName(movieName)).thenReturn(Optional.empty());

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findBy(movieName));
      verify(movieJpaRepository).findByMovieName(movieName);
    }

    @DisplayName("실패: 데이터베이스 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      String movieName = DEFAULT_MOVIE_NAME;
      when(movieJpaRepository.findByMovieName(movieName))
          .thenThrow(new RuntimeException("Database error"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findBy(movieName));
      verify(movieJpaRepository).findByMovieName(movieName);
    }

    @DisplayName("성공: 영화 이름으로 영화를 조회한다")
    @Test
    void test1000() {
      // given
      String movieName = DEFAULT_MOVIE_NAME;
      MovieEntity movieEntity = aMovieEntity();
      when(movieJpaRepository.findByMovieName(movieName)).thenReturn(Optional.of(movieEntity));

      // when
      StreamFixMovie result = sut.findBy(movieName);

      // then
      assertNotNull(result);
      assertEquals(DEFAULT_MOVIE_NAME, result.movieName());
      assertEquals(DEFAULT_IS_ADULT, result.isAdult());
      assertEquals(DEFAULT_GENRE, result.genre());
      assertEquals(DEFAULT_OVERVIEW, result.overview());
      assertEquals(DEFAULT_RELEASE_DATE, result.releasedAt());
      verify(movieJpaRepository).findByMovieName(movieName);
    }
  }

  @Nested
  @DisplayName("insert: 영화 삽입")
  class Insert {

    @DisplayName("실패: 영화 저장 중 데이터베이스 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      StreamFixMovie streamFixMovie = aStreamFixMovie();
      when(movieJpaRepository.findByMovieName(streamFixMovie.movieName()))
          .thenReturn(Optional.empty());
      when(movieJpaRepository.save(any(MovieEntity.class)))
          .thenThrow(new RuntimeException("Database save error"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.insert(streamFixMovie));
    }

    @DisplayName("성공: 이미 존재하는 영화면 저장하지 않는다")
    @Test
    void test1000() {
      // given
      StreamFixMovie streamFixMovie = aStreamFixMovie();
      MovieEntity existingMovie = aMovieEntity();
      when(movieJpaRepository.findByMovieName(streamFixMovie.movieName()))
          .thenReturn(Optional.of(existingMovie));

      // when
      sut.insert(streamFixMovie);

      // then
      verify(movieJpaRepository).findByMovieName(streamFixMovie.movieName());
      verify(movieJpaRepository, never()).save(any(MovieEntity.class));
    }

    @DisplayName("성공: 새로운 영화를 저장한다")
    @Test
    void test1001() {
      // given
      StreamFixMovie streamFixMovie = aStreamFixMovie();
      when(movieJpaRepository.findByMovieName(streamFixMovie.movieName()))
          .thenReturn(Optional.empty());

      // when
      sut.insert(streamFixMovie);

      // then
      verify(movieJpaRepository).findByMovieName(streamFixMovie.movieName());
      verify(movieJpaRepository).save(any(MovieEntity.class));
    }

    @DisplayName("성공: 커스텀 영화 데이터를 저장한다")
    @Test
    void test1002() {
      // given
      StreamFixMovie customMovie = aStreamFixMovieWithName("Custom Movie");
      when(movieJpaRepository.findByMovieName(customMovie.movieName()))
          .thenReturn(Optional.empty());

      // when
      sut.insert(customMovie);

      // then
      verify(movieJpaRepository).findByMovieName("Custom Movie");
      verify(movieJpaRepository).save(any(MovieEntity.class));
    }
  }

  @Nested
  @DisplayName("MovieEntityFixtures: 영화 엔티티 픽스처 테스트")
  class MovieEntityFixtures {

    @DisplayName("기본 영화 엔티티를 생성할 수 있다")
    @Test
    void test1() {
      // when
      MovieEntity movieEntity = aMovieEntity();

      // then
      assertNotNull(movieEntity);
      assertEquals(DEFAULT_MOVIE_NAME, movieEntity.getMovieName());
      assertEquals(DEFAULT_IS_ADULT, movieEntity.getIsAdult());
      assertEquals(DEFAULT_GENRE, movieEntity.getGenre());
      assertEquals(DEFAULT_OVERVIEW, movieEntity.getOverview());
      assertEquals(DEFAULT_RELEASE_DATE, movieEntity.getReleasedAt());
      assertNotNull(movieEntity.getMovieId());
    }

    @DisplayName("특정 이름을 가진 영화 엔티티를 생성할 수 있다")
    @Test
    void test2() {
      // given
      String customName = "Custom Movie Name";

      // when
      MovieEntity movieEntity = aMovieEntityWithName(customName);

      // then
      assertNotNull(movieEntity);
      assertEquals(customName, movieEntity.getMovieName());
      assertEquals(DEFAULT_IS_ADULT, movieEntity.getIsAdult());
      assertEquals(DEFAULT_GENRE, movieEntity.getGenre());
    }

    @DisplayName("성인 영화 엔티티를 생성할 수 있다")
    @Test
    void test3() {
      // when
      MovieEntity adultMovie = anAdultMovieEntity();

      // then
      assertNotNull(adultMovie);
      assertTrue(adultMovie.getIsAdult());
      assertEquals(ADULT_MOVIE_NAME, adultMovie.getMovieName());
    }

    @DisplayName("호러 장르 영화 엔티티를 생성할 수 있다")
    @Test
    void test4() {
      // when
      MovieEntity horrorMovie = aHorrorMovieEntity();

      // then
      assertNotNull(horrorMovie);
      assertEquals(HORROR_MOVIE_NAME, horrorMovie.getMovieName());
      assertEquals(HORROR_GENRE, horrorMovie.getGenre());
    }

    @DisplayName("코미디 장르 영화 엔티티를 생성할 수 있다")
    @Test
    void test5() {
      // when
      MovieEntity comedyMovie = aComedyMovieEntity();

      // then
      assertNotNull(comedyMovie);
      assertEquals(COMEDY_MOVIE_NAME, comedyMovie.getMovieName());
      assertEquals(COMEDY_GENRE, comedyMovie.getGenre());
    }
  }

  @Nested
  @DisplayName("StreamFixMovie Fixtures: 스트림픽스 영화 도메인 픽스처 테스트")
  class StreamFixMovieFixtures {

    @DisplayName("기본 스트림픽스 영화를 생성할 수 있다")
    @Test
    void test1() {
      // when
      StreamFixMovie movie = aStreamFixMovie();

      // then
      assertNotNull(movie);
      assertEquals(DEFAULT_MOVIE_NAME, movie.movieName());
      assertEquals(DEFAULT_IS_ADULT, movie.isAdult());
      assertEquals(DEFAULT_GENRE, movie.genre());
      assertEquals(DEFAULT_OVERVIEW, movie.overview());
      assertEquals(DEFAULT_RELEASE_DATE, movie.releasedAt());
    }

    @DisplayName("특정 이름을 가진 스트림픽스 영화를 생성할 수 있다")
    @Test
    void test2() {
      // given
      String customName = "Custom StreamFix Movie";

      // when
      StreamFixMovie movie = aStreamFixMovieWithName(customName);

      // then
      assertNotNull(movie);
      assertEquals(customName, movie.movieName());
      assertEquals(DEFAULT_IS_ADULT, movie.isAdult());
    }

    @DisplayName("성인 스트림픽스 영화를 생성할 수 있다")
    @Test
    void test3() {
      // when
      StreamFixMovie adultMovie = anAdultStreamFixMovie();

      // then
      assertNotNull(adultMovie);
      assertTrue(adultMovie.isAdult());
      assertEquals(ADULT_MOVIE_NAME, adultMovie.movieName());
    }

    @DisplayName("긴 줄거리를 가진 영화를 생성할 수 있다")
    @Test
    void test4() {
      // when
      StreamFixMovie movieWithLongOverview = aStreamFixMovieWithLongOverview();

      // then
      assertNotNull(movieWithLongOverview);
      assertTrue(movieWithLongOverview.overview().length() > 200);
    }

    @DisplayName("빈 줄거리를 가진 영화를 생성할 수 있다")
    @Test
    void test5() {
      // when
      StreamFixMovie movieWithEmptyOverview = aStreamFixMovieWithEmptyOverview();

      // then
      assertNotNull(movieWithEmptyOverview);
      assertTrue(
          movieWithEmptyOverview.overview().isEmpty()
              || movieWithEmptyOverview.overview().isBlank());
    }
  }
}
