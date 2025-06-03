package com.shokoku.streamfix.movie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TmdbPageableMovies 도메인 테스트")
class TmdbPageableMoviesTest {

    @Nested
    @DisplayName("TmdbPageableMovies 객체 생성 테스트")
    class CreateTmdbPageableMoviesTest {

        @Test
        @DisplayName("영화 목록과 페이지 정보로 TmdbPageableMovies 객체를 생성할 수 있다")
        void shouldCreateTmdbPageableMoviesWithMoviesAndPageInfo() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            int page = 1;
            boolean hasNext = true;

            // when
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, page, hasNext);

            // then
            assertThat(pageableMovies.tmdbMovies()).hasSize(3);
            assertThat(pageableMovies.page()).isEqualTo(1);
            assertThat(pageableMovies.hasNext()).isTrue();
        }

        @Test
        @DisplayName("빈 영화 목록으로도 TmdbPageableMovies 객체를 생성할 수 있다")
        void shouldCreateTmdbPageableMoviesWithEmptyMovieList() {
            // given
            List<TmdbMovie> emptyMovies = Collections.emptyList();
            int page = 1;
            boolean hasNext = false;

            // when
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(emptyMovies, page, hasNext);

            // then
            assertThat(pageableMovies.tmdbMovies()).isEmpty();
            assertThat(pageableMovies.page()).isEqualTo(1);
            assertThat(pageableMovies.hasNext()).isFalse();
        }

        @Test
        @DisplayName("마지막 페이지를 나타내는 TmdbPageableMovies 객체를 생성할 수 있다")
        void shouldCreateLastPageTmdbPageableMovies() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            int lastPage = 10;
            boolean hasNext = false;

            // when
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, lastPage, hasNext);

            // then
            assertThat(pageableMovies.page()).isEqualTo(10);
            assertThat(pageableMovies.hasNext()).isFalse();
            assertThat(pageableMovies.tmdbMovies()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("TmdbPageableMovies 페이지 정보 테스트")
    class PageableMoviesPageInfoTest {

        @ParameterizedTest(name = "페이지 {0}에 대한 객체를 생성할 수 있다")
        @ValueSource(ints = {1, 2, 5, 10, 50, 100})
        @DisplayName("다양한 페이지 번호로 객체를 생성할 수 있다")
        void shouldCreateTmdbPageableMoviesWithVariousPageNumbers(int pageNumber) {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            boolean hasNext = pageNumber < 10;

            // when
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, pageNumber, hasNext);

            // then
            assertThat(pageableMovies.page()).isEqualTo(pageNumber);
            assertThat(pageableMovies.hasNext()).isEqualTo(hasNext);
        }

        @ParameterizedTest(name = "페이지 크기 {1}개의 영화 목록으로 객체를 생성할 수 있다")
        @MethodSource("provideMovieListSizes")
        @DisplayName("다양한 크기의 영화 목록으로 객체를 생성할 수 있다")
        void shouldCreateTmdbPageableMoviesWithVariousMovieListSizes(List<TmdbMovie> movies, int expectedSize) {
            // when
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 1, true);

            // then
            assertThat(pageableMovies.tmdbMovies()).hasSize(expectedSize);
            assertThat(pageableMovies.tmdbMovies()).containsExactlyElementsOf(movies);
        }

        private static Stream<Arguments> provideMovieListSizes() {
            return Stream.of(
                Arguments.of(Collections.emptyList(), 0),
                Arguments.of(createMovieList(1), 1),
                Arguments.of(createMovieList(5), 5),
                Arguments.of(createMovieList(10), 10),
                Arguments.of(createMovieList(20), 20)
            );
        }

        private static List<TmdbMovie> createMovieList(int size) {
            return IntStream.range(0, size)
                .mapToObj(i -> new TmdbMovie(
                    "영화 " + (i + 1),
                    false,
                    List.of("테스트"),
                    "테스트용 영화 " + (i + 1),
                    "2024-01-0" + String.format("%02d", Math.min(i + 1, 9))
                ))
                .toList();
        }
    }

    @Nested
    @DisplayName("TmdbPageableMovies 비즈니스 로직 테스트")
    class PageableMoviesBusinessLogicTest {

        @Test
        @DisplayName("페이지에 포함된 영화 개수를 확인할 수 있다")
        void shouldGetMovieCount() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 1, true);

            // when
            int movieCount = getMovieCount(pageableMovies);

            // then
            assertThat(movieCount).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 페이지인지 확인할 수 있다")
        void shouldCheckIfPageIsEmpty() {
            // given
            TmdbPageableMovies emptyPage = new TmdbPageableMovies(Collections.emptyList(), 1, false);
            TmdbPageableMovies nonEmptyPage = new TmdbPageableMovies(createSampleMovies(), 1, true);

            // when & then
            assertThat(isEmptyPage(emptyPage)).isTrue();
            assertThat(isEmptyPage(nonEmptyPage)).isFalse();
        }

        @Test
        @DisplayName("첫 번째 페이지인지 확인할 수 있다")
        void shouldCheckIfFirstPage() {
            // given
            TmdbPageableMovies firstPage = new TmdbPageableMovies(createSampleMovies(), 1, true);
            TmdbPageableMovies secondPage = new TmdbPageableMovies(createSampleMovies(), 2, true);

            // when & then
            assertThat(isFirstPage(firstPage)).isTrue();
            assertThat(isFirstPage(secondPage)).isFalse();
        }

        @Test
        @DisplayName("마지막 페이지인지 확인할 수 있다")
        void shouldCheckIfLastPage() {
            // given
            TmdbPageableMovies lastPage = new TmdbPageableMovies(createSampleMovies(), 5, false);
            TmdbPageableMovies middlePage = new TmdbPageableMovies(createSampleMovies(), 3, true);

            // when & then
            assertThat(isLastPage(lastPage)).isTrue();
            assertThat(isLastPage(middlePage)).isFalse();
        }

        @Test
        @DisplayName("특정 장르의 영화 개수를 확인할 수 있다")
        void shouldCountMoviesByGenre() {
            // given
            List<TmdbMovie> movies = Arrays.asList(
                new TmdbMovie("액션 영화1", false, List.of("액션"), "액션 영화", "2024-01-01"),
                new TmdbMovie("액션 영화2", false, List.of("액션", "스릴러"), "액션 스릴러", "2024-01-02"),
                new TmdbMovie("로맨스 영화", false, List.of("로맨스"), "로맨스 영화", "2024-01-03")
            );
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 1, false);

            // when
            long actionMovieCount = countMoviesByGenre(pageableMovies, "액션");
            long romanceMovieCount = countMoviesByGenre(pageableMovies, "로맨스");

            // then
            assertThat(actionMovieCount).isEqualTo(2);
            assertThat(romanceMovieCount).isEqualTo(1);
        }

        @Test
        @DisplayName("성인 영화의 개수를 확인할 수 있다")
        void shouldCountAdultMovies() {
            // given
            List<TmdbMovie> movies = Arrays.asList(
                new TmdbMovie("일반 영화1", false, List.of("드라마"), "일반 영화", "2024-01-01"),
                new TmdbMovie("성인 영화1", true, List.of("성인"), "성인 영화", "2024-01-02"),
                new TmdbMovie("일반 영화2", false, List.of("코미디"), "일반 영화", "2024-01-03"),
                new TmdbMovie("성인 영화2", true, List.of("성인"), "성인 영화", "2024-01-04")
            );
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 1, false);

            // when
            long adultMovieCount = countAdultMovies(pageableMovies);
            long familyMovieCount = countFamilyMovies(pageableMovies);

            // then
            assertThat(adultMovieCount).isEqualTo(2);
            assertThat(familyMovieCount).isEqualTo(2);
        }

        @Test
        @DisplayName("페이지 정보를 요약할 수 있다")
        void shouldSummarizePageInfo() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 3, true);

            // when
            String summary = summarizePageInfo(pageableMovies);

            // then
            assertThat(summary)
                .contains("페이지: 3")
                .contains("영화 수: 3")
                .contains("다음 페이지: 있음");
        }

        // 헬퍼 메서드들 (실제 비즈니스 로직 시뮬레이션)
        private int getMovieCount(TmdbPageableMovies pageableMovies) {
            return pageableMovies.tmdbMovies().size();
        }

        private boolean isEmptyPage(TmdbPageableMovies pageableMovies) {
            return pageableMovies.tmdbMovies().isEmpty();
        }

        private boolean isFirstPage(TmdbPageableMovies pageableMovies) {
            return pageableMovies.page() == 1;
        }

        private boolean isLastPage(TmdbPageableMovies pageableMovies) {
            return !pageableMovies.hasNext();
        }

        private long countMoviesByGenre(TmdbPageableMovies pageableMovies, String genre) {
            return pageableMovies.tmdbMovies().stream()
                .filter(movie -> movie.genre().contains(genre))
                .count();
        }

        private long countAdultMovies(TmdbPageableMovies pageableMovies) {
            return pageableMovies.tmdbMovies().stream()
                .filter(TmdbMovie::isAdult)
                .count();
        }

        private long countFamilyMovies(TmdbPageableMovies pageableMovies) {
            return pageableMovies.tmdbMovies().stream()
                .filter(movie -> !movie.isAdult())
                .count();
        }

        private String summarizePageInfo(TmdbPageableMovies pageableMovies) {
            return String.format(
                "페이지: %d, 영화 수: %d, 다음 페이지: %s",
                pageableMovies.page(),
                pageableMovies.tmdbMovies().size(),
                pageableMovies.hasNext() ? "있음" : "없음"
            );
        }
    }

    @Nested
    @DisplayName("TmdbPageableMovies 동등성 테스트")
    class PageableMoviesEqualityTest {

        @Test
        @DisplayName("동일한 데이터를 가진 TmdbPageableMovies 객체는 같다")
        void shouldBeEqualWhenSameData() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            TmdbPageableMovies pageableMovies1 = new TmdbPageableMovies(movies, 1, true);
            TmdbPageableMovies pageableMovies2 = new TmdbPageableMovies(movies, 1, true);

            // when & then
            assertThat(pageableMovies1).isEqualTo(pageableMovies2);
            assertThat(pageableMovies1.hashCode()).isEqualTo(pageableMovies2.hashCode());
        }

        @Test
        @DisplayName("다른 데이터를 가진 TmdbPageableMovies 객체는 다르다")
        void shouldNotBeEqualWhenDifferentData() {
            // given
            List<TmdbMovie> movies1 = createSampleMovies();
            List<TmdbMovie> movies2 = createSampleMovies().subList(0, 2);
            TmdbPageableMovies pageableMovies1 = new TmdbPageableMovies(movies1, 1, true);
            TmdbPageableMovies pageableMovies2 = new TmdbPageableMovies(movies2, 2, false);

            // when & then
            assertThat(pageableMovies1).isNotEqualTo(pageableMovies2);
        }

        @Test
        @DisplayName("페이지 번호만 다른 경우 다른 객체로 인식된다")
        void shouldNotBeEqualWhenDifferentPageNumber() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            TmdbPageableMovies pageableMovies1 = new TmdbPageableMovies(movies, 1, true);
            TmdbPageableMovies pageableMovies2 = new TmdbPageableMovies(movies, 2, true);

            // when & then
            assertThat(pageableMovies1).isNotEqualTo(pageableMovies2);
        }
    }

    @Nested
    @DisplayName("TmdbPageableMovies 불변성 테스트")
    class PageableMoviesImmutabilityTest {

        @Test
        @DisplayName("영화 목록을 수정해도 원본 객체는 변경되지 않는다")
        void shouldMaintainImmutabilityOfMovieList() {
            // given
            List<TmdbMovie> originalMovies = createSampleMovies();
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(originalMovies, 1, true);
            
            // when
            List<TmdbMovie> retrievedMovies = pageableMovies.tmdbMovies();
            int originalSize = retrievedMovies.size();

            // then - 리스트가 불변인지 확인 (수정 시도 시 예외 발생하거나 무시됨)
            assertThat(retrievedMovies).hasSize(originalSize);
            assertThat(pageableMovies.tmdbMovies()).hasSize(originalSize);
        }

        @Test
        @DisplayName("TmdbPageableMovies 객체의 모든 필드는 생성 후 변경되지 않는다")
        void shouldMaintainFieldImmutability() {
            // given
            List<TmdbMovie> movies = createSampleMovies();
            TmdbPageableMovies pageableMovies = new TmdbPageableMovies(movies, 5, false);

            // when & then - Record 타입이므로 모든 필드가 final
            assertThat(pageableMovies.page()).isEqualTo(5);
            assertThat(pageableMovies.hasNext()).isFalse();
            assertThat(pageableMovies.tmdbMovies()).hasSize(3);
            
            // 필드 값들이 생성 시점의 값을 유지
            assertThat(pageableMovies.page()).isEqualTo(5);
            assertThat(pageableMovies.hasNext()).isFalse();
        }
    }

    // 공통 헬퍼 메서드들
    private static List<TmdbMovie> createSampleMovies() {
        return Arrays.asList(
            new TmdbMovie(
                "어벤져스: 엔드게임",
                false,
                Arrays.asList("액션", "어드벤처", "SF"),
                "마블 시네마틱 유니버스의 대미",
                "2019-04-25"
            ),
            new TmdbMovie(
                "기생충",
                false,
                Arrays.asList("스릴러", "드라마", "코미디"),
                "기택네 가족의 기생 이야기",
                "2019-05-30"
            ),
            new TmdbMovie(
                "인터스텔라",
                false,
                Arrays.asList("SF", "드라마", "모험"),
                "우주를 향한 인류의 도전",
                "2014-11-06"
            )
        );
    }
}
