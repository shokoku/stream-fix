package com.shokoku.streamfix.fixtures;

import com.shokoku.streamfix.movie.StreamFixMovie;
import com.shokoku.streamfix.movie.TmdbMovie;
import com.shokoku.streamfix.movie.TmdbPageableMovies;
import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import java.util.List;

/**
 * 영화 관련 테스트 픽스처 클래스
 *
 * <p>영화 도메인의 테스트 데이터를 일관되고 재사용 가능하게 제공합니다. TMDB 영화, StreamFix 영화, 페이지네이션 응답 등 다양한 영화 관련 객체를 지원합니다.
 */
public class MovieFixtures {

  // 기본 테스트 데이터 상수
  public static final String DEFAULT_MOVIE_NAME = "Test Movie";
  public static final String DEFAULT_OVERVIEW = "Test movie overview for testing purposes";
  public static final String DEFAULT_RELEASE_DATE = "2024-01-01";
  public static final List<String> DEFAULT_GENRES = List.of("Action", "Drama");
  public static final String DEFAULT_GENRE_STRING = "Action,Drama";
  public static final boolean DEFAULT_IS_ADULT = false;
  public static final int DEFAULT_PAGE = 1;
  public static final boolean DEFAULT_HAS_NEXT = true;

  // 다양한 영화 데이터 상수
  public static final String ADULT_MOVIE_NAME = "Adult Movie";
  public static final String HORROR_MOVIE_NAME = "Horror Movie";
  public static final String COMEDY_MOVIE_NAME = "Comedy Movie";
  public static final List<String> HORROR_GENRES = List.of("Horror", "Thriller");
  public static final List<String> COMEDY_GENRES = List.of("Comedy", "Romance");

  /** 기본 TMDB 영화 생성 */
  public static TmdbMovie aTmdbMovie() {
    return new TmdbMovie(
        DEFAULT_MOVIE_NAME,
        DEFAULT_IS_ADULT,
        DEFAULT_GENRES,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 특정 이름을 가진 TMDB 영화 생성 */
  public static TmdbMovie aTmdbMovieWithName(String movieName) {
    return new TmdbMovie(
        movieName, DEFAULT_IS_ADULT, DEFAULT_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 성인 영화 TMDB 영화 생성 */
  public static TmdbMovie anAdultTmdbMovie() {
    return new TmdbMovie(
        ADULT_MOVIE_NAME, true, DEFAULT_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 호러 장르 TMDB 영화 생성 */
  public static TmdbMovie aHorrorTmdbMovie() {
    return new TmdbMovie(
        HORROR_MOVIE_NAME, DEFAULT_IS_ADULT, HORROR_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 코미디 장르 TMDB 영화 생성 */
  public static TmdbMovie aComedyTmdbMovie() {
    return new TmdbMovie(
        COMEDY_MOVIE_NAME, DEFAULT_IS_ADULT, COMEDY_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 커스텀 장르를 가진 TMDB 영화 생성 */
  public static TmdbMovie aTmdbMovieWithGenres(List<String> genres) {
    return new TmdbMovie(
        DEFAULT_MOVIE_NAME, DEFAULT_IS_ADULT, genres, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 기본 StreamFix 영화 생성 */
  public static StreamFixMovie aStreamFixMovie() {
    return StreamFixMovie.builder()
        .movieName(DEFAULT_MOVIE_NAME)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE_STRING)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 특정 이름을 가진 StreamFix 영화 생성 */
  public static StreamFixMovie aStreamFixMovieWithName(String movieName) {
    return StreamFixMovie.builder()
        .movieName(movieName)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(DEFAULT_GENRE_STRING)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 성인 영화 StreamFix 영화 생성 */
  public static StreamFixMovie anAdultStreamFixMovie() {
    return StreamFixMovie.builder()
        .movieName(ADULT_MOVIE_NAME)
        .isAdult(true)
        .genre(DEFAULT_GENRE_STRING)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 특정 장르를 가진 StreamFix 영화 생성 */
  public static StreamFixMovie aStreamFixMovieWithGenre(String genre) {
    return StreamFixMovie.builder()
        .movieName(DEFAULT_MOVIE_NAME)
        .isAdult(DEFAULT_IS_ADULT)
        .genre(genre)
        .overview(DEFAULT_OVERVIEW)
        .releasedAt(DEFAULT_RELEASE_DATE)
        .build();
  }

  /** 기본 영화 응답 생성 */
  public static MovieResponse aMovieResponse() {
    return new MovieResponse(
        DEFAULT_MOVIE_NAME,
        DEFAULT_IS_ADULT,
        DEFAULT_GENRES,
        DEFAULT_OVERVIEW,
        DEFAULT_RELEASE_DATE);
  }

  /** 특정 이름을 가진 영화 응답 생성 */
  public static MovieResponse aMovieResponseWithName(String movieName) {
    return new MovieResponse(
        movieName, DEFAULT_IS_ADULT, DEFAULT_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 성인 영화 응답 생성 */
  public static MovieResponse anAdultMovieResponse() {
    return new MovieResponse(
        ADULT_MOVIE_NAME, true, DEFAULT_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 호러 영화 응답 생성 */
  public static MovieResponse aHorrorMovieResponse() {
    return new MovieResponse(
        HORROR_MOVIE_NAME, DEFAULT_IS_ADULT, HORROR_GENRES, DEFAULT_OVERVIEW, DEFAULT_RELEASE_DATE);
  }

  /** 기본 TMDB 페이지네이션 영화 목록 생성 */
  public static TmdbPageableMovies aTmdbPageableMovies() {
    return new TmdbPageableMovies(List.of(aTmdbMovie()), DEFAULT_PAGE, DEFAULT_HAS_NEXT);
  }

  /** 빈 TMDB 페이지네이션 영화 목록 생성 */
  public static TmdbPageableMovies anEmptyTmdbPageableMovies() {
    return new TmdbPageableMovies(List.of(), DEFAULT_PAGE, false);
  }

  /** 여러 영화를 포함한 TMDB 페이지네이션 목록 생성 */
  public static TmdbPageableMovies aTmdbPageableMoviesWithMultipleMovies() {
    return new TmdbPageableMovies(
        List.of(aTmdbMovie(), aHorrorTmdbMovie(), aComedyTmdbMovie()),
        DEFAULT_PAGE,
        DEFAULT_HAS_NEXT);
  }

  /** 특정 페이지를 가진 TMDB 페이지네이션 목록 생성 */
  public static TmdbPageableMovies aTmdbPageableMoviesWithPage(int page) {
    return new TmdbPageableMovies(
        List.of(aTmdbMovie()), page, page < 10 // 10페이지 미만이면 다음 페이지 존재
        );
  }

  /** 기본 페이지네이션 영화 응답 생성 */
  public static PageableMovieResponse aPageableMovieResponse() {
    return new PageableMovieResponse(List.of(aMovieResponse()), DEFAULT_PAGE, DEFAULT_HAS_NEXT);
  }

  /** 빈 페이지네이션 영화 응답 생성 */
  public static PageableMovieResponse anEmptyPageableMovieResponse() {
    return new PageableMovieResponse(List.of(), DEFAULT_PAGE, false);
  }

  /** 여러 영화를 포함한 페이지네이션 응답 생성 */
  public static PageableMovieResponse aPageableMovieResponseWithMultipleMovies() {
    return new PageableMovieResponse(
        List.of(aMovieResponse(), aHorrorMovieResponse()), DEFAULT_PAGE, DEFAULT_HAS_NEXT);
  }

  /** 특정 페이지를 가진 페이지네이션 응답 생성 */
  public static PageableMovieResponse aPageableMovieResponseWithPage(int page) {
    return new PageableMovieResponse(List.of(aMovieResponse()), page, DEFAULT_HAS_NEXT);
  }

  /** 영화 이름 목록으로 영화 응답 목록 생성 */
  public static List<MovieResponse> movieResponsesWithNames(String... movieNames) {
    return List.of(movieNames).stream().map(MovieFixtures::aMovieResponseWithName).toList();
  }

  /** 영화 이름 목록으로 StreamFix 영화 목록 생성 */
  public static List<StreamFixMovie> streamFixMoviesWithNames(String... movieNames) {
    return List.of(movieNames).stream().map(MovieFixtures::aStreamFixMovieWithName).toList();
  }
}
