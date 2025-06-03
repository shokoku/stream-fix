package com.shokoku.streamfix.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SearchSampleUseCase 인터페이스 테스트")
@ExtendWith(MockitoExtension.class)
class SearchSampleUseCaseTest {

  @Nested
  @DisplayName("SearchSampleUseCase 인터페이스 정의 테스트")
  class SearchSampleUseCaseDefinitionTest {

    @Test
    @DisplayName("SearchSampleUseCase 인터페이스가 올바르게 정의되어 있다")
    void shouldHaveCorrectInterfaceDefinition() {
      // given - 인터페이스 클래스 정보 확인
      Class<SearchSampleUseCase> useCaseClass = SearchSampleUseCase.class;

      // when & then
      assertThat(useCaseClass.isInterface()).isTrue();
      assertThat(useCaseClass.getMethods()).hasSize(1);
      assertThat(useCaseClass.getMethods()[0].getName()).isEqualTo("getSample");
      assertThat(useCaseClass.getMethods()[0].getReturnType()).isEqualTo(SampleResponse.class);
      assertThat(useCaseClass.getMethods()[0].getParameterCount()).isZero();
    }

    @Test
    @DisplayName("getSample 메서드가 올바른 시그니처를 가진다")
    void shouldHaveCorrectMethodSignature() {
      // given
      Class<SearchSampleUseCase> useCaseClass = SearchSampleUseCase.class;

      // when
      try {
        var method = useCaseClass.getMethod("getSample");

        // then
        assertThat(method).isNotNull();
        assertThat(method.getReturnType()).isEqualTo(SampleResponse.class);
        assertThat(method.getParameterTypes()).isEmpty();
        assertThat(method.getExceptionTypes()).isEmpty();
      } catch (NoSuchMethodException e) {
        throw new AssertionError("getSample 메서드가 존재하지 않습니다", e);
      }
    }

    @Test
    @DisplayName("UseCase 인터페이스는 함수형 인터페이스가 아니다")
    void shouldNotBeFunctionalInterface() {
      // given
      Class<SearchSampleUseCase> useCaseClass = SearchSampleUseCase.class;

      // when & then
      assertThat(useCaseClass.getAnnotation(FunctionalInterface.class)).isNull();
      // UseCase는 비즈니스 로직을 표현하는 인터페이스이므로 함수형 인터페이스가 아님
    }
  }

  @Nested
  @DisplayName("SearchSampleUseCase 구현체 테스트 시나리오")
  class SearchSampleUseCaseImplementationTest {

    @Test
    @DisplayName("UseCase 구현체는 null을 반환하지 않아야 한다")
    void shouldNotReturnNull() {
      // given - 테스트용 구현체
      SearchSampleUseCase useCase = createTestImplementation("테스트 샘플");

      // when
      SampleResponse response = useCase.getSample();

      // then
      assertThat(response).isNotNull();
      assertThat(response.name()).isEqualTo("테스트 샘플");
    }

    @Test
    @DisplayName("UseCase 구현체는 일관된 결과를 반환해야 한다")
    void shouldReturnConsistentResults() {
      // given
      SearchSampleUseCase useCase = createTestImplementation("일관성 테스트");

      // when - 여러 번 호출
      SampleResponse response1 = useCase.getSample();
      SampleResponse response2 = useCase.getSample();
      SampleResponse response3 = useCase.getSample();

      // then
      assertThat(response1).isEqualTo(response2);
      assertThat(response2).isEqualTo(response3);
      assertThat(response1.name()).isEqualTo("일관성 테스트");
    }

    @Test
    @DisplayName("UseCase 구현체는 빈 문자열도 정상적으로 처리해야 한다")
    void shouldHandleEmptyStringCorrectly() {
      // given
      SearchSampleUseCase useCase = createTestImplementation("");

      // when
      SampleResponse response = useCase.getSample();

      // then
      assertThat(response).isNotNull();
      assertThat(response.name()).isEmpty();
    }

    @Test
    @DisplayName("UseCase 구현체는 null 값도 정상적으로 처리해야 한다")
    void shouldHandleNullValueCorrectly() {
      // given
      SearchSampleUseCase useCase = createTestImplementation(null);

      // when
      SampleResponse response = useCase.getSample();

      // then
      assertThat(response).isNotNull();
      assertThat(response.name()).isNull();
    }
  }

  @Nested
  @DisplayName("SearchSampleUseCase 비즈니스 시나리오 테스트")
  class SearchSampleUseCaseBusinessScenarioTest {

    @Test
    @DisplayName("정상적인 샘플 조회 시나리오")
    void shouldExecuteNormalSampleRetrievalScenario() {
      // given - 정상적인 샘플 데이터
      String expectedSampleName = "스트림픽스 정상 샘플";
      SearchSampleUseCase useCase = createTestImplementation(expectedSampleName);

      // when - 샘플 조회 실행
      SampleResponse response = useCase.getSample();

      // then - 기대값 검증
      assertThat(response).isNotNull();
      assertThat(response.name()).isEqualTo(expectedSampleName);
      assertThat(response.name()).isNotEmpty();
      assertThat(response.name()).contains("스트림픽스");
    }

    @Test
    @DisplayName("다국어 샘플명 처리 시나리오")
    void shouldHandleMultiLanguageSampleNames() {
      // given - 다양한 언어의 샘플명
      String koreanSample = "한국어 샘플";
      String englishSample = "English Sample";
      String japaneseSample = "日本語サンプル";
      String emojiSample = "Emoji Sample 🎬🍿";

      // when & then
      assertThat(createTestImplementation(koreanSample).getSample().name()).isEqualTo(koreanSample);
      assertThat(createTestImplementation(englishSample).getSample().name())
          .isEqualTo(englishSample);
      assertThat(createTestImplementation(japaneseSample).getSample().name())
          .isEqualTo(japaneseSample);
      assertThat(createTestImplementation(emojiSample).getSample().name()).isEqualTo(emojiSample);
    }

    @Test
    @DisplayName("긴 샘플명 처리 시나리오")
    void shouldHandleLongSampleNames() {
      // given - 매우 긴 샘플명
      String longSampleName = "이것은 매우 긴 샘플명입니다. ".repeat(10) + "실제 비즈니스에서는 이런 긴 이름도 처리할 수 있어야 합니다.";

      SearchSampleUseCase useCase = createTestImplementation(longSampleName);

      // when
      SampleResponse response = useCase.getSample();

      // then
      assertThat(response.name()).isEqualTo(longSampleName);
      assertThat(response.name().length()).isGreaterThan(100);
      assertThat(response.name()).startsWith("이것은 매우 긴 샘플명입니다.");
    }

    @Test
    @DisplayName("특수문자가 포함된 샘플명 처리 시나리오")
    void shouldHandleSpecialCharactersInSampleNames() {
      // given - 특수문자가 포함된 샘플명들
      String[] specialSamples = {
        "Sample@Domain.com",
        "Sample#123!@#",
        "Sample with spaces",
        "Sample\twith\ttabs",
        "Sample\nwith\nlines",
        "Sample with 특수문자 & symbols"
      };

      // when & then
      for (String specialSample : specialSamples) {
        SearchSampleUseCase useCase = createTestImplementation(specialSample);
        SampleResponse response = useCase.getSample();

        assertThat(response.name()).isEqualTo(specialSample);
      }
    }
  }

  @Nested
  @DisplayName("SearchSampleUseCase 성능 및 안정성 테스트")
  class SearchSampleUseCasePerformanceTest {

    @Test
    @DisplayName("UseCase는 빠르게 응답해야 한다")
    void shouldRespondQuickly() {
      // given
      SearchSampleUseCase useCase = createTestImplementation("성능 테스트 샘플");

      // when & then - 1000번 호출해도 빠르게 응답
      long startTime = System.currentTimeMillis();

      for (int i = 0; i < 1000; i++) {
        SampleResponse response = useCase.getSample();
        assertThat(response).isNotNull();
      }

      long endTime = System.currentTimeMillis();
      long executionTime = endTime - startTime;

      // 1000번 호출이 1초 이내에 완료되어야 함
      assertThat(executionTime).isLessThan(1000);
    }

    @Test
    @DisplayName("동시 호출 시에도 안전해야 한다")
    void shouldBeSafeForConcurrentCalls() {
      // given
      SearchSampleUseCase useCase = createTestImplementation("동시성 테스트");

      // when - 여러 스레드에서 동시 호출 시뮬레이션
      int numberOfThreads = 10;
      Thread[] threads = new Thread[numberOfThreads];
      SampleResponse[] results = new SampleResponse[numberOfThreads];

      for (int i = 0; i < numberOfThreads; i++) {
        final int index = i;
        threads[i] =
            new Thread(
                () -> {
                  results[index] = useCase.getSample();
                });
      }

      // 모든 스레드 시작
      for (Thread thread : threads) {
        thread.start();
      }

      // 모든 스레드 완료 대기
      for (Thread thread : threads) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        }
      }

      // then - 모든 결과가 동일해야 함
      for (SampleResponse result : results) {
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("동시성 테스트");
      }
    }
  }

  // 테스트용 UseCase 구현체 생성 헬퍼 메서드
  private SearchSampleUseCase createTestImplementation(String sampleName) {
    return () -> new SampleResponse(sampleName);
  }
}
