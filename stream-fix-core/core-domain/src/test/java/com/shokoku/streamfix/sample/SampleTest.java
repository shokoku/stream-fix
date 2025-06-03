package com.shokoku.streamfix.sample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sample 도메인 테스트")
class SampleTest {

  @Nested
  @DisplayName("Sample 객체 생성 테스트")
  class CreateSampleTest {

    @Test
    @DisplayName("정상적인 이름으로 Sample 객체를 생성할 수 있다")
    void shouldCreateSampleWithValidName() {
      // given
      String validName = "스트리밍 서비스 샘플";

      // when
      Sample sample = new Sample(validName);

      // then
      assertThat(sample.name()).isEqualTo("스트리밍 서비스 샘플");
    }

    @ParameterizedTest(name = "특수문자가 포함된 이름으로도 Sample 객체를 생성할 수 있다: {0}")
    @ValueSource(strings = {"Sample-123", "Sample_Test", "Sample@Domain", "사용자#1", "영화&드라마"})
    @DisplayName("특수문자가 포함된 이름으로도 Sample 객체를 생성할 수 있다")
    void shouldCreateSampleWithSpecialCharacters(String nameWithSpecialChars) {
      // when
      Sample sample = new Sample(nameWithSpecialChars);

      // then
      assertThat(sample.name()).isEqualTo(nameWithSpecialChars);
    }

    @ParameterizedTest(name = "빈 값 또는 null 이름으로도 Sample 객체를 생성할 수 있다: \"{0}\"")
    @NullAndEmptySource
    @DisplayName("빈 값 또는 null 이름으로도 Sample 객체를 생성할 수 있다")
    void shouldCreateSampleWithNullOrEmptyName(String invalidName) {
      // when
      Sample sample = new Sample(invalidName);

      // then
      if (invalidName == null) {
        assertThat(sample.name()).isNull();
      } else {
        assertThat(sample.name()).isEmpty();
      }
    }
  }

  @Nested
  @DisplayName("Sample 객체 동등성 테스트")
  class SampleEqualityTest {

    @Test
    @DisplayName("같은 이름을 가진 두 Sample 객체는 동일하다")
    void shouldBeEqualWhenSameName() {
      // given
      String name = "동일한 샘플";
      Sample sample1 = new Sample(name);
      Sample sample2 = new Sample(name);

      // when & then
      assertThat(sample1).isEqualTo(sample2);
      assertThat(sample1.hashCode()).isEqualTo(sample2.hashCode());
    }

    @Test
    @DisplayName("다른 이름을 가진 두 Sample 객체는 다르다")
    void shouldNotBeEqualWhenDifferentName() {
      // given
      Sample sample1 = new Sample("샘플1");
      Sample sample2 = new Sample("샘플2");

      // when & then
      assertThat(sample1).isNotEqualTo(sample2);
    }

    @Test
    @DisplayName("null과 비교했을 때 다르다")
    void shouldNotBeEqualToNull() {
      // given
      Sample sample = new Sample("테스트 샘플");

      // when & then
      assertThat(sample).isNotEqualTo(null);
    }
  }

  @Nested
  @DisplayName("Sample 객체 문자열 표현 테스트")
  class SampleToStringTest {

    @Test
    @DisplayName("toString()이 적절한 형식으로 반환된다")
    void shouldReturnProperStringRepresentation() {
      // given
      String sampleName = "스트림픽스 샘플";
      Sample sample = new Sample(sampleName);

      // when
      String result = sample.toString();

      // then
      assertThat(result).contains("Sample").contains(sampleName);
    }
  }

  @Nested
  @DisplayName("Sample 객체 비즈니스 로직 테스트")
  class SampleBusinessLogicTest {

    @Test
    @DisplayName("이름이 유효한지 확인할 수 있다")
    void shouldCheckIfNameIsValid() {
      // given
      Sample validSample = new Sample("유효한 샘플");
      Sample nullSample = new Sample(null);
      Sample emptySample = new Sample("");
      Sample blankSample = new Sample("   ");

      // when & then
      assertThat(isValidSampleName(validSample)).isTrue();
      assertThat(isValidSampleName(nullSample)).isFalse();
      assertThat(isValidSampleName(emptySample)).isFalse();
      assertThat(isValidSampleName(blankSample)).isFalse();
    }

    @Test
    @DisplayName("이름의 길이를 확인할 수 있다")
    void shouldGetNameLength() {
      // given
      Sample sample = new Sample("스트리밍");

      // when
      int length = getNameLength(sample);

      // then
      assertThat(length).isEqualTo(4);
    }

    @Test
    @DisplayName("null 이름의 경우 길이는 0이다")
    void shouldReturnZeroLengthForNullName() {
      // given
      Sample sample = new Sample(null);

      // when
      int length = getNameLength(sample);

      // then
      assertThat(length).isZero();
    }

    // 헬퍼 메서드들 (실제 도메인 로직을 시뮬레이션)
    private boolean isValidSampleName(Sample sample) {
      return sample.name() != null && !sample.name().trim().isEmpty();
    }

    private int getNameLength(Sample sample) {
      return sample.name() != null ? sample.name().length() : 0;
    }
  }
}
