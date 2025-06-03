package com.shokoku.streamfix.movie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TmdbMovie 도메인 테스트")
class TmdbMovieTest {

    @Nested
    @DisplayName("TmdbMovie 객체 생성 테스트")
    class CreateTmdbMovieTest {

        @Test
        @DisplayName("모든 필드가 정상적으로 입력된 경우 TmdbMovie 객체를 생성할 수 있다")
        void shouldCreateTmdbMovieWithAllValidFields() {
            // given
            String movieName = "어벤져스: 엔드게임";
            boolean isAdult = false;
            List<String> genres = Arrays.asList("액션", "어드벤처", "SF");
            String overview = "마블 시네마틱 유니버스의 대미를 장식하는 영화";
            String releaseAt = "2019-04-25";

            // when
            TmdbMovie movie = new TmdbMovie(movieName, isAdult, genres, overview, releaseAt);

            // then
            assertThat(movie.movieName()).isEqualTo("어벤져스: 엔드게임");
            assertThat(movie.isAdult()).isFalse();
            assertThat(movie.genre()).containsExactly("액션", "어드벤처", "SF");
            assertThat(movie.overview()).isEqualTo("마블 시네마틱 유니버스의 대미를 장식하는 영화");
            assertThat(movie.releaseAt()).isEqualTo("2019-04-25");
        }

        @Test
        @DisplayName("성인 영화로 TmdbMovie 객체를 생성할 수 있다")
        void shouldCreateAdultTmdbMovie() {
            // given & when
            TmdbMovie adultMovie = new TmdbMovie(
                "성인 영화", 
                true, 
                List.of("드라마"), 
                "성인 대상 영화", 
                "2024-01-01"
            );

            // then
            assertThat(adultMovie.isAdult()).isTrue();
            assertThat(adultMovie.movieName()).isEqualTo("성인 영화");
        }

        @Test
        @DisplayName("장르가 없는 영화도 TmdbMovie 객체를 생성할 수 있다")
        void shouldCreateTmdbMovieWithEmptyGenres() {
            // given & when
            TmdbMovie movie = new TmdbMovie(
                "장르 미정", 
                false, 
                Collections.emptyList(), 
                "장르가 정해지지 않은 영화", 
                "2024-12-25"
            );

            // then
            assertThat(movie.genre()).isEmpty();
            assertThat(movie.movieName()).isEqualTo("장르 미정");
        }
    }

    @Nested
    @DisplayName("TmdbMovie 필드 검증 테스트")
    class TmdbMovieFieldValidationTest {

        @ParameterizedTest(name = "한국어 영화 제목으로 객체를 생성할 수 있다: {0}")
        @ValueSource(strings = {
            "기생충",
            "미나리", 
            "올드보이",
            "타짜: 원 아이드 잭",
            "신과함께-죄와 벌"
        })
        @DisplayName("다양한 한국어 영화 제목으로 객체를 생성할 수 있다")
        void shouldCreateTmdbMovieWithKoreanTitles(String koreanTitle) {
            // when
            TmdbMovie movie = new TmdbMovie(
                koreanTitle, 
                false, 
                List.of("드라마"), 
                "한국 영화", 
                "2020-01-01"
            );

            // then
            assertThat(movie.movieName()).isEqualTo(koreanTitle);
        }

        @ParameterizedTest(name = "다양한 장르 조합으로 영화를 생성할 수 있다")
        @MethodSource("provideGenreCombinations")
        @DisplayName("다양한 장르 조합으로 영화를 생성할 수 있다")
        void shouldCreateTmdbMovieWithVariousGenres(List<String> genres, int expectedSize) {
            // when
            TmdbMovie movie = new TmdbMovie(
                "테스트 영화", 
                false, 
                genres, 
                "테스트용 영화", 
                "2024-01-01"
            );

            // then
            assertThat(movie.genre()).hasSize(expectedSize);
            assertThat(movie.genre()).containsExactlyElementsOf(genres);
        }

        private static Stream<Arguments> provideGenreCombinations() {
            return Stream.of(
                Arguments.of(List.of("액션"), 1),
                Arguments.of(Arrays.asList("액션", "코미디"), 2),
                Arguments.of(Arrays.asList("SF", "스릴러", "액션"), 3),
                Arguments.of(Arrays.asList("로맨스", "코미디", "드라마", "가족"), 4),
                Arguments.of(Collections.emptyList(), 0)
            );
        }

        @Test
        @DisplayName("장편 영화 개요를 가진 영화를 생성할 수 있다")
        void shouldCreateTmdbMovieWithLongOverview() {
            // given
            String longOverview = "이것은 매우 긴 영화 개요입니다. ".repeat(10) + 
                                 "영화의 스토리, 캐릭터, 배경 등을 상세히 설명하는 내용이 포함되어 있습니다.";

            // when
            TmdbMovie movie = new TmdbMovie(
                "서사 대작", 
                false, 
                List.of("드라마", "서사"), 
                longOverview, 
                "2024-06-01"
            );

            // then
            assertThat(movie.overview()).isEqualTo(longOverview);
            assertThat(movie.overview().length()).isGreaterThan(100);
        }
    }

    @Nested
    @DisplayName("TmdbMovie 개봉일 관련 테스트")
    class TmdbMovieReleaseDateTest {

        @ParameterizedTest(name = "유효한 날짜 형식으로 영화를 생성할 수 있다: {0}")
        @ValueSource(strings = {
            "2024-01-01",
            "2023-12-31", 
            "2025-06-15",
            "1999-01-01",
            "2030-12-25"
        })
        @DisplayName("유효한 날짜 형식으로 영화를 생성할 수 있다")
        void shouldCreateTmdbMovieWithValidDateFormat(String releaseDate) {
            // when
            TmdbMovie movie = new TmdbMovie(
                "날짜 테스트 영화", 
                false, 
                List.of("테스트"), 
                "날짜 형식 테스트", 
                releaseDate
            );

            // then
            assertThat(movie.releaseAt()).isEqualTo(releaseDate);
            assertThat(isValidDateFormat(movie.releaseAt())).isTrue();
        }

        @Test
        @DisplayName("개봉일이 미래인 영화를 식별할 수 있다")
        void shouldIdentifyUpcomingMovie() {
            // given
            String futureDate = "2030-12-25";
            TmdbMovie futureMovie = new TmdbMovie(
                "미래 영화", 
                false, 
                List.of("SF"), 
                "미래에 개봉할 영화", 
                futureDate
            );

            // when & then
            assertThat(isUpcomingMovie(futureMovie)).isTrue();
        }

        @Test
        @DisplayName("개봉일이 과거인 영화를 식별할 수 있다")
        void shouldIdentifyReleasedMovie() {
            // given
            String pastDate = "2020-01-01";
            TmdbMovie pastMovie = new TmdbMovie(
                "과거 영화", 
                false, 
                List.of("드라마"), 
                "이미 개봉한 영화", 
                pastDate
            );

            // when & then
            assertThat(isUpcomingMovie(pastMovie)).isFalse();
        }

        // 헬퍼 메서드들
        private boolean isValidDateFormat(String date) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate.parse(date, formatter);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isUpcomingMovie(TmdbMovie movie) {
            try {
                LocalDate releaseDate = LocalDate.parse(movie.releaseAt());
                return releaseDate.isAfter(LocalDate.now());
            } catch (Exception e) {
                return false;
            }
        }
    }

    @Nested
    @DisplayName("TmdbMovie 비즈니스 로직 테스트")
    class TmdbMovieBusinessLogicTest {

        @Test
        @DisplayName("영화가 특정 장르를 포함하는지 확인할 수 있다")
        void shouldCheckIfMovieContainsGenre() {
            // given
            TmdbMovie actionMovie = new TmdbMovie(
                "액션 영화", 
                false, 
                Arrays.asList("액션", "스릴러"), 
                "액션과 스릴러가 포함된 영화", 
                "2024-01-01"
            );

            // when & then
            assertThat(containsGenre(actionMovie, "액션")).isTrue();
            assertThat(containsGenre(actionMovie, "스릴러")).isTrue();
            assertThat(containsGenre(actionMovie, "로맨스")).isFalse();
        }

        @Test
        @DisplayName("영화의 장르 개수를 확인할 수 있다")
        void shouldGetGenreCount() {
            // given
            TmdbMovie multiGenreMovie = new TmdbMovie(
                "복합 장르 영화", 
                false, 
                Arrays.asList("액션", "코미디", "드라마", "로맨스"), 
                "여러 장르가 섞인 영화", 
                "2024-01-01"
            );

            // when
            int genreCount = getGenreCount(multiGenreMovie);

            // then
            assertThat(genreCount).isEqualTo(4);
        }

        @Test
        @DisplayName("영화 제목의 길이를 확인할 수 있다")
        void shouldGetMovieTitleLength() {
            // given
            TmdbMovie movie = new TmdbMovie(
                "매우 긴 제목을 가진 영화", 
                false, 
                List.of("드라마"), 
                "제목이 긴 영화", 
                "2024-01-01"
            );

            // when
            int titleLength = getTitleLength(movie);

            // then
            assertThat(titleLength).isEqualTo("매우 긴 제목을 가진 영화".length()); // 실제 길이로 수정
        }

        @Test
        @DisplayName("성인 영화 여부를 확인할 수 있다")
        void shouldCheckAdultContent() {
            // given
            TmdbMovie adultMovie = new TmdbMovie(
                "성인 영화", 
                true, 
                List.of("성인"), 
                "성인 대상", 
                "2024-01-01"
            );
            TmdbMovie familyMovie = new TmdbMovie(
                "가족 영화", 
                false, 
                List.of("가족"), 
                "전체 관람가", 
                "2024-01-01"
            );

            // when & then
            assertThat(isFamilyFriendly(adultMovie)).isFalse();
            assertThat(isFamilyFriendly(familyMovie)).isTrue();
        }

        // 헬퍼 메서드들 (실제 비즈니스 로직 시뮬레이션)
        private boolean containsGenre(TmdbMovie movie, String genre) {
            return movie.genre().contains(genre);
        }

        private int getGenreCount(TmdbMovie movie) {
            return movie.genre().size();
        }

        private int getTitleLength(TmdbMovie movie) {
            return movie.movieName() != null ? movie.movieName().length() : 0;
        }

        private boolean isFamilyFriendly(TmdbMovie movie) {
            return !movie.isAdult();
        }
    }

    @Nested
    @DisplayName("TmdbMovie 동등성 및 해시코드 테스트")
    class TmdbMovieEqualityTest {

        @Test
        @DisplayName("동일한 정보를 가진 두 TmdbMovie 객체는 같다")
        void shouldBeEqualWhenSameContent() {
            // given
            List<String> genres = Arrays.asList("액션", "드라마");
            TmdbMovie movie1 = new TmdbMovie(
                "동일 영화", false, genres, "같은 영화", "2024-01-01"
            );
            TmdbMovie movie2 = new TmdbMovie(
                "동일 영화", false, genres, "같은 영화", "2024-01-01"
            );

            // when & then
            assertThat(movie1).isEqualTo(movie2);
            assertThat(movie1.hashCode()).isEqualTo(movie2.hashCode());
        }

        @Test
        @DisplayName("다른 정보를 가진 두 TmdbMovie 객체는 다르다")
        void shouldNotBeEqualWhenDifferentContent() {
            // given
            TmdbMovie movie1 = new TmdbMovie(
                "영화1", false, List.of("액션"), "첫 번째 영화", "2024-01-01"
            );
            TmdbMovie movie2 = new TmdbMovie(
                "영화2", false, List.of("드라마"), "두 번째 영화", "2024-01-02"
            );

            // when & then
            assertThat(movie1).isNotEqualTo(movie2);
        }
    }
}
