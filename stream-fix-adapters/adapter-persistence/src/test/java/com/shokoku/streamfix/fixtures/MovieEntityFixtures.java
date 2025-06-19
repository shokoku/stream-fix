package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.entity.movie.MovieEntity;
import com.shokoku.streamfix.movie.StreamFixMovie;

/**
 * 영화 엔티티 관련 테스트 픽스처 클래스
 *
 * <p>영화 엔티티와 도메인 객체의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다. MovieEntity와 StreamFixMovie 객체를 다양한 시나리오로 생성할 수
 * 있습니다.
 */
public class MovieEntityFixtures {

  // 기본 테스트 데이터 상수
  public static final String DEFAULT_MOVIE_ID = "movie-123";
  public static final String DEFAULT_MOVIE_NAME = "Test Movie";
  public static final boolean DEFAULT_IS_ADULT = false;
  public static final String DEFAULT_GENRE = "Action,Drama";
  public static final String DEFAULT_OVERVIEW = "Test movie overview for testing purposes";
  public static final String DEFAULT_RELEASE_DATE = "2024-01-01";

  // 다양한 영화 데이터 상수
  public static final String ADULT_MOVIE_NAME = "Adult Movie";
  public static final String HORROR_MOVIE_NAME = "Horror Movie";
  public static final String COMEDY_MOVIE_NAME = "Comedy Movie";
  public static final String HORROR_GENRE = "Horror,Thriller";
  public static final String COMEDY_GENRE = "Comedy,Romance";
  public static final String LONG_OVERVIEW =
      "This is a very long movie overview that exceeds the normal length limit. ".repeat(10);
  public static final String EMPTY_OVERVIEW = "";

  /** 기본 영화 엔티티 생성 */
  public static MovieEntity aMovieEntity() {
    return new MovieEntity(
        DEFAULT_MOVIE_ID,
        DEFAULT_MOVIE_NAME,
        DEFAULT_IS_ADULT,
        DEFAULT_GENRE,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 특정 이름을 가진 영화 엔티티 생성 */
  public static MovieEntity aMovieEntityWithName(String movieName) {
    return new MovieEntity(
        DEFAULT_MOVIE_ID,
        movieName,
        DEFAULT_IS_ADULT,
        DEFAULT_GENRE,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 성인 영화 엔티티 생성 */
  public static MovieEntity anAdultMovieEntity() {
    return new MovieEntity(
        DEFAULT_MOVIE_ID,
        ADULT_MOVIE_NAME,
        true,
        DEFAULT_GENRE,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 호러 장르 영화 엔티티 생성 */
  public static MovieEntity aHorrorMovieEntity() {
    return new MovieEntity(
        DEFAULT_MOVIE_ID,
        HORROR_MOVIE_NAME,
        DEFAULT_IS_ADULT,
        HORROR_GENRE,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 코미디 장르 영화 엔티티 생성 */
  public static MovieEntity aComedyMovieEntity() {
    return new MovieEntity(
        DEFAULT_MOVIE_ID,
        COMEDY_MOVIE_NAME,
        DEFAULT_IS_ADULT,
        COMEDY_GENRE,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  // StreamFixMovie 도메인 객체 픽스처들

  /** 기본 StreamFixMovie 도메인 객체 생성 */
  public static StreamFixMovie aStreamFixMovie() {
    return StreamFixMovie.builder()
        .movieName(DEFAULT_MOVIE_NAME)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 특정 이름을 가진 StreamFixMovie 생성 */
  public static StreamFixMovie aStreamFixMovieWithName(String movieName) {
    return StreamFixMovie.builder()
        .movieName(movieName)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 성인 StreamFixMovie 생성 */
  public static StreamFixMovie anAdultStreamFixMovie() {
    return StreamFixMovie.builder()
        .movieName(ADULT_MOVIE_NAME)
        .isAdult(true)
        .genre(DEFAULT_GENRE)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 긴 줄거리를 가진 StreamFixMovie 생성 */
  public static StreamFixMovie aStreamFixMovieWithLongOverview() {
    return StreamFixMovie.builder()
        .movieName(DEFAULT_MOVIE_NAME)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE)
        .overview(LONG_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 빈 줄거리를 가진 StreamFixMovie 생성 */
  public static StreamFixMovie aStreamFixMovieWithEmptyOverview() {
    return StreamFixMovie.builder()
        .movieName(DEFAULT_MOVIE_NAME)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE)
        .overview(EMPTY_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }
}
