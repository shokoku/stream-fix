package com.shokoku.streamfix.repository.token;

import static com.shokoku.streamfix.fixtures.TokenEntityFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.entity.token.TokenEntity;
import com.shokoku.streamfix.token.TokenPortResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenRepositoryTest {

  @InjectMocks TokenRepository sut;

  @Mock TokenJpaRepository tokenJpaRepository;

  @Nested
  @DisplayName("create: 새로운 토큰 생성")
  class CreateToken {

    @DisplayName("실패: DB 저장 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(tokenJpaRepository.save(any(TokenEntity.class)))
          .thenThrow(new RuntimeException("DB 저장 실패"));

      // when & then
      assertThrows(
          RuntimeException.class,
          () -> sut.create(DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN));
    }

    @DisplayName("성공: 유효한 파라미터로 토큰을 생성하고 TokenPortResponse를 반환한다")
    @Test
    void test1000() {
      // given
      TokenEntity mockTokenEntity = aTokenEntity();
      when(tokenJpaRepository.save(any(TokenEntity.class))).thenReturn(mockTokenEntity);

      // when
      TokenPortResponse result =
          sut.create(DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);

      // then
      assertNotNull(result);
      assertEquals(DEFAULT_ACCESS_TOKEN, result.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, result.refreshToken());
      verify(tokenJpaRepository).save(any(TokenEntity.class));
    }

    @DisplayName("성공: 생성된 TokenEntity의 만료 시간이 올바르게 설정된다")
    @Test
    void test1001() {
      // given
      LocalDateTime beforeCreate = LocalDateTime.now();
      TokenEntity savedEntity = aTokenEntity();
      when(tokenJpaRepository.save(any(TokenEntity.class))).thenReturn(savedEntity);

      // when
      sut.create(DEFAULT_USER_ID, DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN);

      // then
      verify(tokenJpaRepository)
          .save(
              argThat(
                  tokenEntity -> {
                    LocalDateTime afterCreate = LocalDateTime.now();

                    // accessToken 만료시간 검증 (3시간 후)
                    LocalDateTime expectedAccessExpiry = beforeCreate.plusHours(3);
                    LocalDateTime actualAccessExpiry = tokenEntity.getAccessTokenExpiresAt();
                    assertTrue(actualAccessExpiry.isAfter(expectedAccessExpiry.minusMinutes(1)));
                    assertTrue(
                        actualAccessExpiry.isBefore(afterCreate.plusHours(3).plusMinutes(1)));

                    // refreshToken 만료시간 검증 (24시간 후)
                    LocalDateTime expectedRefreshExpiry = beforeCreate.plusHours(24);
                    LocalDateTime actualRefreshExpiry = tokenEntity.getRefreshTokenExpiresAt();
                    assertTrue(actualRefreshExpiry.isAfter(expectedRefreshExpiry.minusMinutes(1)));
                    assertTrue(
                        actualRefreshExpiry.isBefore(afterCreate.plusHours(24).plusMinutes(1)));

                    return true;
                  }));
    }

    @DisplayName("성공: 다양한 사용자로 토큰을 생성할 수 있다")
    @Test
    void test1002() {
      // given
      TokenEntity mockTokenEntity = aTokenEntityWithUserId(CUSTOM_USER_ID);
      when(tokenJpaRepository.save(any(TokenEntity.class))).thenReturn(mockTokenEntity);

      // when
      TokenPortResponse result = sut.create(CUSTOM_USER_ID, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);

      // then
      assertNotNull(result);
      assertEquals(NEW_ACCESS_TOKEN, result.accessToken());
      assertEquals(NEW_REFRESH_TOKEN, result.refreshToken());
      verify(tokenJpaRepository)
          .save(
              argThat(
                  entity ->
                      CUSTOM_USER_ID.equals(entity.getUserId())
                          && NEW_ACCESS_TOKEN.equals(entity.getAccessToken())
                          && NEW_REFRESH_TOKEN.equals(entity.getRefreshToken())));
    }
  }

  @Nested
  @DisplayName("findByUserId: 사용자 ID로 토큰 조회")
  class FindByUserId {

    @DisplayName("실패: DB 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(tokenJpaRepository.findByUserId(DEFAULT_USER_ID))
          .thenThrow(new RuntimeException("DB 조회 실패"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findByUserId(DEFAULT_USER_ID));
    }

    @DisplayName("성공: 존재하지 않는 사용자 ID로 조회하면 null을 반환한다")
    @Test
    void test1000() {
      // given
      String nonExistentUserId = aNonExistentUserId();
      when(tokenJpaRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

      // when
      TokenPortResponse result = sut.findByUserId(nonExistentUserId);

      // then
      assertNull(result);
      verify(tokenJpaRepository).findByUserId(nonExistentUserId);
    }

    @DisplayName("성공: 존재하는 사용자 ID로 조회하면 TokenPortResponse를 반환한다")
    @Test
    void test1001() {
      // given
      TokenEntity tokenEntity = aTokenEntity();
      when(tokenJpaRepository.findByUserId(DEFAULT_USER_ID)).thenReturn(Optional.of(tokenEntity));

      // when
      TokenPortResponse result = sut.findByUserId(DEFAULT_USER_ID);

      // then
      assertNotNull(result);
      assertEquals(DEFAULT_ACCESS_TOKEN, result.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, result.refreshToken());
      verify(tokenJpaRepository).findByUserId(DEFAULT_USER_ID);
    }

    @DisplayName("성공: 다른 사용자의 토큰을 조회할 수 있다")
    @Test
    void test1002() {
      // given
      String customUserId = "anotherUser789";
      String customAccessToken = "another.access.token";
      String customRefreshToken = "another.refresh.token";
      TokenEntity customTokenEntity =
          aTokenEntityWith(customUserId, customAccessToken, customRefreshToken);
      when(tokenJpaRepository.findByUserId(customUserId))
          .thenReturn(Optional.of(customTokenEntity));

      // when
      TokenPortResponse result = sut.findByUserId(customUserId);

      // then
      assertNotNull(result);
      assertEquals(customAccessToken, result.accessToken());
      assertEquals(customRefreshToken, result.refreshToken());
      verify(tokenJpaRepository).findByUserId(customUserId);
    }
  }

  @Nested
  @DisplayName("updateToken: 기존 토큰 업데이트")
  class UpdateToken {

    final String userId = DEFAULT_USER_ID;

    @DisplayName("실패: 존재하지 않는 사용자 ID로 업데이트하면 RuntimeException을 던진다")
    @Test
    void test1() {
      // given
      String nonExistentUserId = aNonExistentUserId();
      when(tokenJpaRepository.findByUserId(nonExistentUserId)).thenReturn(Optional.empty());

      // when & then
      assertThrows(
          RuntimeException.class,
          () -> sut.updateToken(nonExistentUserId, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN));
      verify(tokenJpaRepository).findByUserId(nonExistentUserId);
      verify(tokenJpaRepository, never()).save(any(TokenEntity.class));
    }

    @DisplayName("실패: DB 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      when(tokenJpaRepository.findByUserId(userId)).thenThrow(new RuntimeException("DB 조회 실패"));

      // when & then
      assertThrows(
          RuntimeException.class,
          () -> sut.updateToken(userId, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN));
    }

    @DisplayName("실패: DB 저장 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test3() {
      // given
      TokenEntity tokenEntity = aTokenEntity();
      when(tokenJpaRepository.findByUserId(userId)).thenReturn(Optional.of(tokenEntity));
      when(tokenJpaRepository.save(any(TokenEntity.class)))
          .thenThrow(new RuntimeException("DB 저장 실패"));

      // when & then
      assertThrows(
          RuntimeException.class,
          () -> sut.updateToken(userId, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN));
    }

    @DisplayName("성공: 유효한 파라미터로 토큰을 업데이트한다")
    @Test
    void test1000() {
      // given
      TokenEntity tokenEntity = aTokenEntity();
      when(tokenJpaRepository.findByUserId(userId)).thenReturn(Optional.of(tokenEntity));
      when(tokenJpaRepository.save(any(TokenEntity.class))).thenReturn(tokenEntity);

      // when
      assertDoesNotThrow(() -> sut.updateToken(userId, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN));

      // then
      verify(tokenJpaRepository).findByUserId(userId);
      verify(tokenJpaRepository).save(tokenEntity);
    }

    @DisplayName("성공: 업데이트된 TokenEntity의 토큰 값이 새로운 값으로 변경된다")
    @Test
    void test1001() {
      // given
      TokenEntity tokenEntity = aTokenEntity();
      when(tokenJpaRepository.findByUserId(userId)).thenReturn(Optional.of(tokenEntity));

      // when
      sut.updateToken(userId, UPDATED_ACCESS_TOKEN, UPDATED_REFRESH_TOKEN);

      // then
      assertEquals(UPDATED_ACCESS_TOKEN, tokenEntity.getAccessToken());
      assertEquals(UPDATED_REFRESH_TOKEN, tokenEntity.getRefreshToken());
      verify(tokenJpaRepository).save(tokenEntity);
    }

    @DisplayName("성공: 업데이트된 TokenEntity의 만료 시간이 새롭게 설정된다")
    @Test
    void test1002() {
      // given
      TokenEntity tokenEntity = aTokenEntity();
      LocalDateTime oldAccessExpiry = tokenEntity.getAccessTokenExpiresAt();
      LocalDateTime oldRefreshExpiry = tokenEntity.getRefreshTokenExpiresAt();

      when(tokenJpaRepository.findByUserId(userId)).thenReturn(Optional.of(tokenEntity));

      // when
      LocalDateTime beforeUpdate = LocalDateTime.now();
      sut.updateToken(userId, UPDATED_ACCESS_TOKEN, UPDATED_REFRESH_TOKEN);
      LocalDateTime afterUpdate = LocalDateTime.now();

      // then
      // 새로운 만료 시간이 이전보다 나중이어야 함
      assertTrue(tokenEntity.getAccessTokenExpiresAt().isAfter(oldAccessExpiry));
      assertTrue(tokenEntity.getRefreshTokenExpiresAt().isAfter(oldRefreshExpiry));

      // 새로운 만료 시간이 적절한 범위 내에 있어야 함
      assertTrue(
          tokenEntity.getAccessTokenExpiresAt().isAfter(beforeUpdate.plusHours(3).minusMinutes(1)));
      assertTrue(
          tokenEntity.getAccessTokenExpiresAt().isBefore(afterUpdate.plusHours(3).plusMinutes(1)));

      assertTrue(
          tokenEntity
              .getRefreshTokenExpiresAt()
              .isAfter(beforeUpdate.plusHours(24).minusMinutes(1)));
      assertTrue(
          tokenEntity
              .getRefreshTokenExpiresAt()
              .isBefore(afterUpdate.plusHours(24).plusMinutes(1)));
    }

    @DisplayName("성공: 다른 사용자의 토큰을 업데이트할 수 있다")
    @Test
    void test1003() {
      // given
      String customUserId = "updateUser123";
      TokenEntity customTokenEntity = aTokenEntityWithUserId(customUserId);
      when(tokenJpaRepository.findByUserId(customUserId))
          .thenReturn(Optional.of(customTokenEntity));

      // when
      sut.updateToken(customUserId, NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);

      // then
      assertEquals(NEW_ACCESS_TOKEN, customTokenEntity.getAccessToken());
      assertEquals(NEW_REFRESH_TOKEN, customTokenEntity.getRefreshToken());
      verify(tokenJpaRepository).findByUserId(customUserId);
      verify(tokenJpaRepository).save(customTokenEntity);
    }
  }

  @Nested
  @DisplayName("Port 인터페이스 구현 검증")
  class PortInterfaceImplementation {

    @DisplayName("SearchTokenPort 인터페이스를 구현한다")
    @Test
    void implementsSearchTokenPort() {
      assertInstanceOf(com.shokoku.streamfix.token.SearchTokenPort.class, sut);
    }

    @DisplayName("InsertTokenPort 인터페이스를 구현한다")
    @Test
    void implementsInsertTokenPort() {
      assertInstanceOf(com.shokoku.streamfix.token.InsertTokenPort.class, sut);
    }

    @DisplayName("UpdateTokenPort 인터페이스를 구현한다")
    @Test
    void implementsUpdateTokenPort() {
      assertInstanceOf(com.shokoku.streamfix.token.UpdateTokenPort.class, sut);
    }
  }

  @Nested
  @DisplayName("Transaction 동작 검증")
  class TransactionBehavior {

    @DisplayName("create 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void createMethodHasTransactionalAnnotation() {
      try {
        var method =
            TokenRepository.class.getMethod("create", String.class, String.class, String.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("create 메소드를 찾을 수 없습니다");
      }
    }

    @DisplayName("findByUserId 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void findByUserIdMethodHasTransactionalAnnotation() {
      try {
        var method = TokenRepository.class.getMethod("findByUserId", String.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("findByUserId 메소드를 찾을 수 없습니다");
      }
    }

    @DisplayName("updateToken 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void updateTokenMethodHasTransactionalAnnotation() {
      try {
        var method =
            TokenRepository.class.getMethod(
                "updateToken", String.class, String.class, String.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("updateToken 메소드를 찾을 수 없습니다");
      }
    }
  }

  @Nested
  @DisplayName("TokenPortResponse Fixtures: 다양한 토큰 응답 생성 테스트")
  class TokenPortResponseFixtures {

    @DisplayName("기본 토큰 포트 응답을 생성할 수 있다")
    @Test
    void test1() {
      // when
      TokenPortResponse response = aTokenPortResponse();

      // then
      assertEquals(DEFAULT_ACCESS_TOKEN, response.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, response.refreshToken());
    }

    @DisplayName("특정 액세스 토큰을 가진 응답을 생성할 수 있다")
    @Test
    void test2() {
      // given
      String customAccessToken = "customAccessToken123";

      // when
      TokenPortResponse response = aTokenPortResponseWithAccessToken(customAccessToken);

      // then
      assertEquals(customAccessToken, response.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, response.refreshToken());
    }

    @DisplayName("특정 리프레시 토큰을 가진 응답을 생성할 수 있다")
    @Test
    void test3() {
      // given
      String customRefreshToken = "customRefreshToken456";

      // when
      TokenPortResponse response = aTokenPortResponseWithRefreshToken(customRefreshToken);

      // then
      assertEquals(DEFAULT_ACCESS_TOKEN, response.accessToken());
      assertEquals(customRefreshToken, response.refreshToken());
    }

    @DisplayName("커스텀 토큰들을 가진 응답을 생성할 수 있다")
    @Test
    void test4() {
      // given
      String customAccessToken = "custom.access.token";
      String customRefreshToken = "custom.refresh.token";

      // when
      TokenPortResponse response = aTokenPortResponseWith(customAccessToken, customRefreshToken);

      // then
      assertEquals(customAccessToken, response.accessToken());
      assertEquals(customRefreshToken, response.refreshToken());
    }
  }

  @Nested
  @DisplayName("TokenEntity Fixtures: 다양한 토큰 엔티티 생성 테스트")
  class TokenEntityFixtures {

    @DisplayName("기본 토큰 엔티티를 생성할 수 있다")
    @Test
    void test1() {
      // when
      TokenEntity entity = aTokenEntity();

      // then
      assertEquals(DEFAULT_USER_ID, entity.getUserId());
      assertEquals(DEFAULT_ACCESS_TOKEN, entity.getAccessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, entity.getRefreshToken());
      assertNotNull(entity.getTokenId());
      assertNotNull(entity.getAccessTokenExpiresAt());
      assertNotNull(entity.getRefreshTokenExpiresAt());
    }

    @DisplayName("특정 사용자 ID를 가진 토큰 엔티티를 생성할 수 있다")
    @Test
    void test2() {
      // given
      String customUserId = "customUser456";

      // when
      TokenEntity entity = aTokenEntityWithUserId(customUserId);

      // then
      assertEquals(customUserId, entity.getUserId());
      assertEquals(DEFAULT_ACCESS_TOKEN, entity.getAccessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, entity.getRefreshToken());
    }

    @DisplayName("커스텀 파라미터로 토큰 엔티티를 생성할 수 있다")
    @Test
    void test3() {
      // given
      String customUserId = "customUser789";
      String customAccessToken = "custom.access.token";
      String customRefreshToken = "custom.refresh.token";

      // when
      TokenEntity entity = aTokenEntityWith(customUserId, customAccessToken, customRefreshToken);

      // then
      assertEquals(customUserId, entity.getUserId());
      assertEquals(customAccessToken, entity.getAccessToken());
      assertEquals(customRefreshToken, entity.getRefreshToken());
    }

    @DisplayName("만료된 토큰 엔티티를 생성할 수 있다")
    @Test
    void test4() {
      // when
      TokenEntity entity = anExpiredTokenEntity();

      // then
      assertEquals(DEFAULT_USER_ID, entity.getUserId());
      assertTrue(entity.getAccessTokenExpiresAt().isBefore(LocalDateTime.now()));
      assertTrue(entity.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now()));
    }
  }
}
