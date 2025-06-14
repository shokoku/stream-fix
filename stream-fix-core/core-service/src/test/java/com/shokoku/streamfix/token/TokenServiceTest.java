package com.shokoku.streamfix.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.support.JwtTestHelper;
import com.shokoku.streamfix.user.FetchUserUseCase;
import com.shokoku.streamfix.user.response.UserResponse;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

  @InjectMocks TokenService sut;

  @Mock InsertTokenPort insertTokenPort;
  @Mock UpdateTokenPort updateTokenPort;
  @Mock SearchTokenPort searchTokenPort;
  @Mock KakaoTokenPort kakaoTokenPort;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(sut, "secretKey", JwtTestHelper.TEST_SECRET);
  }

  @Nested
  @DisplayName("createNewToken: 신규 토큰 생성")
  class CreateNewToken {
    final String userId = "testUser123";

    @DisplayName("실패: userId가 null 이거나 비어 있으면 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidUserId) {
      assertThrows(IllegalArgumentException.class, () -> sut.createNewToken(invalidUserId));
    }

    @DisplayName("실패: secretKey가 설정되지 않으면 IllegalArgumentException을 던진다")
    @Test
    void test2() {
      // given
      ReflectionTestUtils.setField(sut, "secretKey", null);

      // when & then
      assertThrows(IllegalArgumentException.class, () -> sut.createNewToken(userId));
    }

    @DisplayName("실패: 토큰 저장 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test3() {
      // given
      when(insertTokenPort.create(eq(userId), anyString(), anyString()))
          .thenThrow(new RuntimeException("DB 저장 실패"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.createNewToken(userId));
    }

    @DisplayName("성공: 유효한 userId와 secretKey로 accessToken과 refreshToken을 반환한다")
    @Test
    void test1000() {
      // given
      TokenPortResponse mockResponse = new TokenPortResponse("mockAccessToken", "mockRefreshToken");
      when(insertTokenPort.create(eq(userId), anyString(), anyString())).thenReturn(mockResponse);

      // when
      var result = sut.createNewToken(userId);

      // then
      assertNotNull(result);
      assertEquals("mockAccessToken", result.accessToken());
      assertEquals("mockRefreshToken", result.refreshToken());
      verify(insertTokenPort).create(eq(userId), anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("validateToken: 토큰 유효성 검증")
  class ValidateToken {

    @DisplayName("실패: 토큰이 null 이거나 빈 값이면 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    void test1(String invalidToken) {
      assertThrows(IllegalArgumentException.class, () -> sut.validateToken(invalidToken));
    }

    @DisplayName("실패: JWT 형식이 아니면 MalformedJwtException을 던진다")
    @Test
    void test2() {
      String invalidToken = "this-is-not-a-jwt-token";
      assertThrows(MalformedJwtException.class, () -> sut.validateToken(invalidToken));
    }

    @DisplayName("실패: 토큰이 만료되었으면 ExpiredJwtException을 던진다")
    @Test
    void test3() {
      String expiredToken = JwtTestHelper.generateExpiredToken("testUser");
      assertThrows(
          io.jsonwebtoken.ExpiredJwtException.class, () -> sut.validateToken(expiredToken));
    }

    @DisplayName("실패: 서명이 잘못되었으면 SignatureException을 던진다")
    @Test
    void test4() {
      String validToken = JwtTestHelper.generateValidToken("testUser");
      String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";
      assertThrows(SignatureException.class, () -> sut.validateToken(tamperedToken));
    }

    @DisplayName("성공: 유효한 토큰이면 true를 반환한다")
    @Test
    void test1000() {
      String validToken = JwtTestHelper.generateValidToken("testUser");
      assertDoesNotThrow(() -> sut.validateToken(validToken));
      assertTrue(sut.validateToken(validToken));
    }
  }

  @Nested
  @DisplayName("getTokenFromKakao: 카카오 토큰 조회")
  class GetTokenFromKakao {
    final String authCode = "testAuthCode";

    @DisplayName("실패: 카카오 API 호출 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(kakaoTokenPort.getAccessTokenByCode(authCode))
          .thenThrow(new RuntimeException("Kakao API 에러"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.getTokenFromKakao(authCode));
    }

    @DisplayName("성공: 유효한 인증 코드로 카카오 액세스 토큰을 가져온다")
    @Test
    void test1000() {
      // given
      String expectedToken = "kakaoAccessToken";
      when(kakaoTokenPort.getAccessTokenByCode(authCode)).thenReturn(expectedToken);

      // when
      String result = sut.getTokenFromKakao(authCode);

      // then
      assertEquals(expectedToken, result);
      verify(kakaoTokenPort).getAccessTokenByCode(authCode);
    }
  }

  @Nested
  @DisplayName("findUserByAccessToken: 토큰으로 사용자 조회")
  class FindUserByAccessToken {

    @Mock private FetchUserUseCase fetchUserUseCase;

    @BeforeEach
    void setUpFetchUserUseCase() {
      ReflectionTestUtils.setField(sut, "fetchUserUseCase", fetchUserUseCase);
    }

    @DisplayName("실패: userId 클레임이 없으면 RuntimeException을 던진다")
    @Test
    void test1() {
      String tokenWithoutUserId = JwtTestHelper.generateTokenWithoutUserId();
      assertThrows(RuntimeException.class, () -> sut.findUserByAccessToken(tokenWithoutUserId));
    }

    @DisplayName("실패: 사용자를 찾을 수 없으면 fetchUserUseCase에서 반환하는 결과에 따라 처리")
    @Test
    void test2() {
      // given
      String validToken = JwtTestHelper.generateValidToken("nonExistentUser");
      when(fetchUserUseCase.findByProviderId("nonExistentUser")).thenReturn(null);

      // when
      UserResponse result = sut.findUserByAccessToken(validToken);

      // then
      assertNull(result);
      verify(fetchUserUseCase).findByProviderId("nonExistentUser");
    }

    @DisplayName("성공: 만료된 토큰이어도 Claims 파싱은 가능하다")
    @Test
    void test3() {
      // given
      String expiredToken = JwtTestHelper.generateExpiredToken("testProviderId");
      UserResponse mockUser =
          UserResponse.builder().providerId("testProviderId").username("testUser").build();
      when(fetchUserUseCase.findByProviderId("testProviderId")).thenReturn(mockUser);

      // when & then
      assertDoesNotThrow(() -> sut.findUserByAccessToken(expiredToken));

      UserResponse result = sut.findUserByAccessToken(expiredToken);
      assertNotNull(result);
      assertEquals("testProviderId", result.providerId());
    }

    @DisplayName("성공: 유효한 토큰으로 사용자를 조회한다")
    @Test
    void test1000() {
      // given
      String validToken = JwtTestHelper.generateValidToken("testProviderId");
      UserResponse mockUser =
          UserResponse.builder().providerId("testProviderId").username("testUser").build();
      when(fetchUserUseCase.findByProviderId("testProviderId")).thenReturn(mockUser);

      // when
      UserResponse result = sut.findUserByAccessToken(validToken);

      // then
      assertNotNull(result);
      assertEquals("testProviderId", result.providerId());
      verify(fetchUserUseCase).findByProviderId("testProviderId");
    }
  }

  @Nested
  @DisplayName("upsertToken: 토큰 생성 또는 업데이트")
  class UpsertToken {
    final String providerId = "testProviderId";

    @DisplayName("실패: 토큰 조회 중 DB 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(searchTokenPort.findByUserId(providerId)).thenThrow(new RuntimeException("DB 조회 실패"));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.upsertToken(providerId));
    }

    @DisplayName("성공: 기존 토큰이 없으면 새로 생성한다")
    @Test
    void test1000() {
      // given
      when(searchTokenPort.findByUserId(providerId)).thenReturn(null);
      when(insertTokenPort.create(anyString(), anyString(), anyString()))
          .thenReturn(new TokenPortResponse("new-access", "new-refresh"));

      // when
      String resultToken = sut.upsertToken(providerId);

      // then
      assertNotNull(resultToken, "새로 생성된 토큰은 null이 아니어야 합니다.");
      verify(searchTokenPort, times(1)).findByUserId(providerId);
      verify(insertTokenPort, times(1)).create(eq(providerId), anyString(), anyString());
      verify(updateTokenPort, never()).updateToken(anyString(), anyString(), anyString());
    }

    @DisplayName("성공: 기존 토큰이 있으면 업데이트한다")
    @Test
    void test1001() {
      // given
      TokenPortResponse existingToken = new TokenPortResponse("oldAccessToken", "oldRefreshToken");
      when(searchTokenPort.findByUserId(providerId)).thenReturn(existingToken);

      // when
      String resultToken = sut.upsertToken(providerId);

      // then
      assertNotNull(resultToken, "업데이트 후 반환된 토큰은 null이 아니어야 합니다.");
      verify(searchTokenPort, times(1)).findByUserId(providerId);
      verify(updateTokenPort, times(1)).updateToken(eq(providerId), anyString(), anyString());
      verify(insertTokenPort, never()).create(anyString(), anyString(), anyString());
    }
  }
}
