package com.shokoku.streamfix.token;

import static com.shokoku.streamfix.fixtures.TokenFixtures.*;
import static com.shokoku.streamfix.fixtures.TokenFixtures.DEFAULT_USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.fixtures.UserFixtures;
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

    final String userId = DEFAULT_USER_ID;

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
      TokenPortResponse mockResponse = aTokenPortResponse();
      when(insertTokenPort.create(eq(userId), anyString(), anyString())).thenReturn(mockResponse);

      // when
      var result = sut.createNewToken(userId);

      // then
      assertNotNull(result);
      assertEquals(DEFAULT_ACCESS_TOKEN, result.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, result.refreshToken());
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

    @DisplayName("실패: null 토큰으로 검증하면 IllegalArgumentException을 던진다")
    @Test
    void test2() {
      String nullToken = aNullToken();
      assertThrows(IllegalArgumentException.class, () -> sut.validateToken(nullToken));
    }

    @DisplayName("실패: 빈 토큰으로 검증하면 IllegalArgumentException을 던진다")
    @Test
    void test3() {
      String emptyToken = anEmptyToken();
      assertThrows(IllegalArgumentException.class, () -> sut.validateToken(emptyToken));
    }

    @DisplayName("실패: JWT 형식이 아니면 MalformedJwtException을 던진다")
    @Test
    void test4() {
      String invalidToken = anInvalidFormatToken();
      assertThrows(MalformedJwtException.class, () -> sut.validateToken(invalidToken));
    }

    @DisplayName("실패: 토큰이 만료되었으면 ExpiredJwtException을 던진다")
    @Test
    void test5() {
      String expiredToken = anExpiredJwtToken();
      assertThrows(
          io.jsonwebtoken.ExpiredJwtException.class, () -> sut.validateToken(expiredToken));
    }

    @DisplayName("성공: 곧 만료될 토큰도 아직 유효 기간 내라면 검증을 통과한다")
    @Test
    void test6() {
      String soonToExpireToken = aSoonToExpireJwtToken();
      assertDoesNotThrow(() -> sut.validateToken(soonToExpireToken));
      assertTrue(sut.validateToken(soonToExpireToken));
    }

    @DisplayName("실패: 서명이 잘못되었으면 SignatureException을 던진다")
    @Test
    void test7() {
      String validToken = aValidJwtToken();
      String tamperedToken = validToken.substring(0, validToken.length() - 1) + "X";
      assertThrows(SignatureException.class, () -> sut.validateToken(tamperedToken));
    }

    @DisplayName("실패: 잘못된 서명을 가진 토큰이면 SignatureException을 던진다")
    @Test
    void test8() {
      String wrongSignatureToken = aJwtTokenWithWrongSignature();
      assertThrows(SignatureException.class, () -> sut.validateToken(wrongSignatureToken));
    }

    @DisplayName("성공: 유효한 토큰이면 true를 반환한다")
    @Test
    void test1000() {
      String validToken = aValidJwtToken();
      assertDoesNotThrow(() -> sut.validateToken(validToken));
      assertTrue(sut.validateToken(validToken));
    }

    @DisplayName("성공: 장기간 유효한 토큰이면 true를 반환한다")
    @Test
    void test1001() {
      String longLivedToken = aLongLivedJwtToken();
      assertDoesNotThrow(() -> sut.validateToken(longLivedToken));
      assertTrue(sut.validateToken(longLivedToken));
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
      String expectedToken = aKakaoAccessToken();
      when(kakaoTokenPort.getAccessTokenByCode(authCode)).thenReturn(expectedToken);

      // when
      String result = sut.getTokenFromKakao(authCode);

      // then
      assertEquals(expectedToken, result);
      verify(kakaoTokenPort).getAccessTokenByCode(authCode);
    }

    @DisplayName("성공: 커스텀 카카오 토큰으로 요청할 수 있다")
    @Test
    void test1001() {
      // given
      String customToken = "customKakaoToken456";
      String expectedToken = aKakaoAccessTokenWith(customToken);
      when(kakaoTokenPort.getAccessTokenByCode(authCode)).thenReturn(expectedToken);

      // when
      String result = sut.getTokenFromKakao(authCode);

      // then
      assertEquals(customToken, result);
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
      String tokenWithoutUserId = aJwtTokenWithoutUserId();
      assertThrows(RuntimeException.class, () -> sut.findUserByAccessToken(tokenWithoutUserId));
    }

    @DisplayName("실패: 사용자를 찾을 수 없으면 fetchUserUseCase에서 반환하는 결과에 따라 처리")
    @Test
    void test2() {
      // given
      String validToken = aValidJwtTokenForUser("nonExistentUser");
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
      String expiredToken = anExpiredJwtTokenForUser(DEFAULT_PROVIDER_ID);
      UserResponse mockUser = UserFixtures.aUserResponseWithProviderId(DEFAULT_PROVIDER_ID);
      when(fetchUserUseCase.findByProviderId(DEFAULT_PROVIDER_ID)).thenReturn(mockUser);

      // when & then
      assertDoesNotThrow(() -> sut.findUserByAccessToken(expiredToken));

      UserResponse result = sut.findUserByAccessToken(expiredToken);
      assertNotNull(result);
      assertEquals(DEFAULT_PROVIDER_ID, result.providerId());
    }

    @DisplayName("성공: 유효한 토큰으로 사용자를 조회한다")
    @Test
    void test1000() {
      // given
      String validToken = aValidJwtTokenForUser(DEFAULT_PROVIDER_ID);
      UserResponse mockUser = UserFixtures.aUserResponseWithProviderId(DEFAULT_PROVIDER_ID);
      when(fetchUserUseCase.findByProviderId(DEFAULT_PROVIDER_ID)).thenReturn(mockUser);

      // when
      UserResponse result = sut.findUserByAccessToken(validToken);

      // then
      assertNotNull(result);
      assertEquals(DEFAULT_PROVIDER_ID, result.providerId());
      verify(fetchUserUseCase).findByProviderId(DEFAULT_PROVIDER_ID);
    }
  }

  @Nested
  @DisplayName("upsertToken: 토큰 생성 또는 업데이트")
  class UpsertToken {

    final String providerId = DEFAULT_PROVIDER_ID;

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
      TokenPortResponse newTokenResponse = aTokenPortResponseWith("new-access", "new-refresh");
      when(insertTokenPort.create(anyString(), anyString(), anyString()))
          .thenReturn(newTokenResponse);

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
      TokenPortResponse existingToken = aTokenPortResponseWith("oldAccessToken", "oldRefreshToken");
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

  @Nested
  @DisplayName("TokenPortResponse Fixtures: 다양한 토큰 응답 생성 테스트")
  class TokenPortResponseFixtures {

    @DisplayName("특정 액세스 토큰을 가진 응답을 생성할 수 있다")
    @Test
    void test1() {
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
    void test2() {
      // given
      String customRefreshToken = "customRefreshToken456";

      // when
      TokenPortResponse response = aTokenPortResponseWithRefreshToken(customRefreshToken);

      // then
      assertEquals(DEFAULT_ACCESS_TOKEN, response.accessToken());
      assertEquals(customRefreshToken, response.refreshToken());
    }

    @DisplayName("유효한 JWT 액세스 토큰을 가진 응답을 생성할 수 있다")
    @Test
    void test3() {
      // when
      TokenPortResponse response = aTokenPortResponseWithValidJwt();

      // then
      assertNotNull(response.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, response.refreshToken());
      assertDoesNotThrow(() -> sut.validateToken(response.accessToken()));
    }

    @DisplayName("특정 사용자 ID로 생성된 유효한 JWT 토큰 응답을 생성할 수 있다")
    @Test
    void test4() {
      // given
      String customUserId = "customUser789";

      // when
      TokenPortResponse response = aTokenPortResponseWithValidJwtForUser(customUserId);

      // then
      assertNotNull(response.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, response.refreshToken());
      assertDoesNotThrow(() -> sut.validateToken(response.accessToken()));
    }

    @DisplayName("만료된 JWT 액세스 토큰을 가진 응답을 생성할 수 있다")
    @Test
    void test5() {
      // when
      TokenPortResponse response = aTokenPortResponseWithExpiredJwt();

      // then
      assertNotNull(response.accessToken());
      assertEquals(DEFAULT_REFRESH_TOKEN, response.refreshToken());
      assertThrows(
          io.jsonwebtoken.ExpiredJwtException.class,
          () -> sut.validateToken(response.accessToken()));
    }
  }

  @Nested
  @DisplayName("JWT Token Edge Cases: JWT 토큰 엣지 케이스 테스트")
  class JwtTokenEdgeCases {

    @DisplayName("곧 만료될 JWT 토큰은 유효 기간 내에서는 정상적으로 작동한다")
    @Test
    void test1() {
      // when
      String soonToExpireToken = aSoonToExpireJwtToken();

      // then
      assertNotNull(soonToExpireToken);
      // 곧 만료될 토큰이지만 아직 유효 기간 내이므로 정상적으로 검증됨
      assertDoesNotThrow(() -> sut.validateToken(soonToExpireToken));
      assertTrue(sut.validateToken(soonToExpireToken));
    }

    @DisplayName("장기간 유효한 JWT 토큰의 특성을 확인할 수 있다")
    @Test
    void test2() {
      // when
      String longLivedToken = aLongLivedJwtToken();

      // then
      assertNotNull(longLivedToken);
      assertDoesNotThrow(() -> sut.validateToken(longLivedToken));
      assertTrue(sut.validateToken(longLivedToken));
    }

    @DisplayName("잘못된 서명을 가진 JWT 토큰 검증 시 SignatureException이 발생한다")
    @Test
    void test3() {
      // when
      String wrongSignatureToken = aJwtTokenWithWrongSignature();

      // then
      assertNotNull(wrongSignatureToken);
      assertThrows(SignatureException.class, () -> sut.validateToken(wrongSignatureToken));
    }

    @DisplayName("특정 사용자의 만료된 토큰으로 사용자 정보를 조회할 수 있다")
    @Test
    void test4() {
      // given
      String customUserId = "expiredTokenUser";
      String expiredToken = anExpiredJwtTokenForUser(customUserId);
      UserResponse mockUser = UserFixtures.aUserResponseWithProviderId(customUserId);

      ReflectionTestUtils.setField(sut, "fetchUserUseCase", mock(FetchUserUseCase.class));
      FetchUserUseCase fetchUserUseCase =
          (FetchUserUseCase) ReflectionTestUtils.getField(sut, "fetchUserUseCase");
      assertNotNull(fetchUserUseCase);
      when(fetchUserUseCase.findByProviderId(customUserId)).thenReturn(mockUser);

      // when & then
      assertDoesNotThrow(() -> sut.findUserByAccessToken(expiredToken));

      UserResponse result = sut.findUserByAccessToken(expiredToken);
      assertNotNull(result);
      assertEquals(customUserId, result.providerId());
    }
  }
}
