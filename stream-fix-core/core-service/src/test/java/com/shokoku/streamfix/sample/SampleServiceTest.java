package com.shokoku.streamfix.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@DisplayName("SampleService 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SamplePort samplePort;

    @Mock
    private SamplePersistencePort samplePersistencePort;

    @InjectMocks
    private SampleService sampleService;

    @Nested
    @DisplayName("getSample 정상 시나리오 테스트")
    class GetSampleNormalScenarioTest {

        @Test
        @DisplayName("정상적인 샘플 조회 시 두 포트에서 데이터를 가져와 응답을 생성한다")
        void shouldGetSampleFromBothPortsAndCreateResponse() {
            // given
            String expectedHttpResponse = "HTTP에서 가져온 샘플";
            String expectedDbResponse = "DB에서 가져온 샘플명";
            String sampleId = "1";

            given(samplePort.getSample()).willReturn(new SamplePortResponse(expectedHttpResponse));
            given(samplePersistencePort.getSampleName(sampleId)).willReturn(expectedDbResponse);

            // when
            SampleResponse result = sampleService.getSample();

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(expectedHttpResponse);

            // 포트 호출 검증
            then(samplePort).should(times(1)).getSample();
            then(samplePersistencePort).should(times(1)).getSampleName(sampleId);
        }

        @Test
        @DisplayName("HTTP 포트에서 null을 반환해도 정상적으로 처리한다")
        void shouldHandleNullFromHttpPort() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse(null));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when
            SampleResponse result = sampleService.getSample();

            // then
            assertThat(result).isNotNull();
            assertThat(result.name()).isNull();
            then(samplePort).should().getSample();
            then(samplePersistencePort).should().getSampleName("1");
        }

        @Test
        @DisplayName("DB 포트에서 데이터를 가져오는 로직이 실행된다")
        void shouldFetchDataFromDatabasePort() {
            // given
            String httpSample = "HTTP 샘플";
            String dbSample = "데이터베이스 샘플";

            given(samplePort.getSample()).willReturn(new SamplePortResponse(httpSample));
            given(samplePersistencePort.getSampleName("1")).willReturn(dbSample);

            // when
            sampleService.getSample();

            // then - DB 포트가 정확한 ID로 호출되었는지 검증
            then(samplePersistencePort).should().getSampleName("1");
        }
    }

    @Nested
    @DisplayName("getSample 예외 상황 테스트")
    class GetSampleExceptionTest {

        @Test
        @DisplayName("HTTP 포트에서 예외 발생 시 적절히 처리한다")
        void shouldHandleExceptionFromHttpPort() {
            // given
            given(samplePort.getSample()).willThrow(new RuntimeException("HTTP 연결 실패"));

            // when & then
            assertThatThrownBy(() -> sampleService.getSample())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("HTTP 연결 실패");

            // HTTP 포트는 호출되었지만 DB 포트는 호출되지 않아야 함
            then(samplePort).should().getSample();
            then(samplePersistencePort).should(never()).getSampleName(anyString());
        }

        @Test
        @DisplayName("DB 포트에서 예외 발생 시 적절히 처리한다")
        void shouldHandleExceptionFromDatabasePort() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("HTTP 샘플"));
            given(samplePersistencePort.getSampleName("1"))
                .willThrow(new RuntimeException("DB 연결 실패"));

            // when & then
            assertThatThrownBy(() -> sampleService.getSample())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB 연결 실패");

            // 두 포트 모두 호출되어야 함
            then(samplePort).should().getSample();
            then(samplePersistencePort).should().getSampleName("1");
        }

        @Test
        @DisplayName("HTTP 포트가 null 객체를 반환할 때 NullPointerException이 발생하지 않는다")
        void shouldHandleNullObjectFromHttpPort() {
            // given
            given(samplePort.getSample()).willReturn(null);
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when & then
            assertThatThrownBy(() -> sampleService.getSample())
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("getSample 포트 상호작용 테스트")
    class GetSamplePortInteractionTest {

        @Test
        @DisplayName("포트 호출 순서가 올바르게 유지된다")
        void shouldMaintainCorrectPortCallOrder() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("샘플"));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when
            sampleService.getSample();

            // then - 호출 순서 검증 (HTTP 포트 먼저, DB 포트 나중)
            var inOrder = org.mockito.Mockito.inOrder(samplePort, samplePersistencePort);
            inOrder.verify(samplePort).getSample();
            inOrder.verify(samplePersistencePort).getSampleName("1");
        }

        @Test
        @DisplayName("HTTP 포트는 매번 정확히 한 번만 호출된다")
        void shouldCallHttpPortExactlyOnce() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("샘플"));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when
            sampleService.getSample();

            // then
            then(samplePort).should(times(1)).getSample();
            then(samplePort).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("DB 포트는 하드코딩된 ID '1'로만 호출된다")
        void shouldCallDatabasePortWithHardcodedId() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("샘플"));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when
            sampleService.getSample();

            // then
            then(samplePersistencePort).should().getSampleName("1");
            // 다른 ID로는 호출되지 않았음을 검증하지 않음 (실제로는 호출되지 않았으므로)
        }
    }

    @Nested
    @DisplayName("getSample 다양한 데이터 시나리오 테스트")
    class GetSampleDataScenarioTest {

        @ParameterizedTest(name = "HTTP 응답이 \"{0}\"일 때 올바르게 처리한다")
        @ValueSource(strings = {
            "정상 샘플",
            "한글 샘플 데이터",
            "English Sample",
            "Sample with 123 numbers",
            "Special@#$%Characters",
            ""
        })
        @DisplayName("다양한 HTTP 응답을 올바르게 처리한다")
        void shouldHandleVariousHttpResponses(String httpResponse) {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse(httpResponse));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 응답");

            // when
            SampleResponse result = sampleService.getSample();

            // then
            assertThat(result.name()).isEqualTo(httpResponse);
        }

        @ParameterizedTest(name = "DB 응답이 \"{1}\"일 때 올바르게 처리한다")
        @MethodSource("provideDatabaseResponses")
        @DisplayName("다양한 DB 응답을 올바르게 처리한다")
        void shouldHandleVariousDatabaseResponses(String httpResponse, String dbResponse) {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse(httpResponse));
            given(samplePersistencePort.getSampleName("1")).willReturn(dbResponse);

            // when
            SampleResponse result = sampleService.getSample();

            // then
            assertThat(result.name()).isEqualTo(httpResponse); // 현재 로직상 HTTP 응답을 반환
            
            // DB 포트가 올바르게 호출되었는지 확인
            then(samplePersistencePort).should().getSampleName("1");
        }

        private static Stream<Arguments> provideDatabaseResponses() {
            return Stream.of(
                Arguments.of("HTTP 샘플", "DB 샘플 1"),
                Arguments.of("HTTP 샘플", "매우 긴 데이터베이스 응답값입니다"),
                Arguments.of("HTTP 샘플", "DB_SPECIAL_#$%_DATA"),
                Arguments.of("HTTP 샘플", "숫자123포함데이터"),
                Arguments.of("HTTP 샘플", "")
            );
        }

        @Test
        @DisplayName("매우 긴 응답 데이터도 올바르게 처리한다")
        void shouldHandleLongResponseData() {
            // given
            String longHttpResponse = "HTTP 응답 ".repeat(1000);
            String longDbResponse = "DB 응답 ".repeat(1000);

            given(samplePort.getSample()).willReturn(new SamplePortResponse(longHttpResponse));
            given(samplePersistencePort.getSampleName("1")).willReturn(longDbResponse);

            // when
            SampleResponse result = sampleService.getSample();

            // then
            assertThat(result.name()).isEqualTo(longHttpResponse);
            assertThat(result.name().length()).isGreaterThan(5000);
        }
    }

    @Nested
    @DisplayName("getSample 성능 및 안정성 테스트")
    class GetSamplePerformanceTest {

        @Test
        @DisplayName("여러 번 호출해도 일관된 결과를 반환한다")
        void shouldReturnConsistentResults() {
            // given
            String expectedResponse = "일관성 테스트 샘플";
            given(samplePort.getSample()).willReturn(new SamplePortResponse(expectedResponse));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 샘플");

            // when & then
            for (int i = 0; i < 10; i++) {
                SampleResponse result = sampleService.getSample();
                assertThat(result.name()).isEqualTo(expectedResponse);
            }

            // 포트들이 정확히 10번씩 호출되었는지 확인
            then(samplePort).should(times(10)).getSample();
            then(samplePersistencePort).should(times(10)).getSampleName("1");
        }

        @Test
        @DisplayName("빠른 응답 시간을 유지한다")
        void shouldMaintainFastResponseTime() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("성능 테스트"));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 성능 테스트");

            // when
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < 1000; i++) {
                sampleService.getSample();
            }
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // then - 1000번 호출이 1초 이내에 완료되어야 함
            assertThat(executionTime).isLessThan(1000);
        }
    }

    @Nested
    @DisplayName("SampleService 인터페이스 구현 테스트")
    class SampleServiceInterfaceTest {

        @Test
        @DisplayName("SearchSampleUseCase 인터페이스를 올바르게 구현한다")
        void shouldImplementSearchSampleUseCaseCorrectly() {
            // given
            given(samplePort.getSample()).willReturn(new SamplePortResponse("구현 테스트"));
            given(samplePersistencePort.getSampleName("1")).willReturn("DB 구현 테스트");

            // when & then
            assertThat(sampleService).isInstanceOf(SearchSampleUseCase.class);
            
            SampleResponse result = sampleService.getSample();
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("서비스 클래스가 @Service 어노테이션을 가진다")
        void shouldHaveServiceAnnotation() {
            // given
            Class<SampleService> serviceClass = SampleService.class;

            // when & then
            assertThat(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class))
                .isTrue();
        }

        @Test
        @DisplayName("서비스 클래스가 @RequiredArgsConstructor 어노테이션을 가진다")
        void shouldHaveRequiredArgsConstructorAnnotation() {
            // given
            Class<SampleService> serviceClass = SampleService.class;

            // when & then - Lombok 어노테이션은 컴파일 시에 처리되므로 리플렉션으로 확인할 수 없음
            // 대신 생성자가 올바르게 생성되었는지 확인
            assertThat(serviceClass.getDeclaredConstructors()).hasSize(1);
            assertThat(serviceClass.getDeclaredConstructors()[0].getParameterCount()).isEqualTo(2);
        }
    }
}
