package com.shokoku.streamfix.repository.user;

import static com.shokoku.streamfix.fixtures.UserEntityFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.shokoku.streamfix.entity.user.SocialUserEntity;
import com.shokoku.streamfix.entity.user.UserEntity;
import com.shokoku.streamfix.repository.subscription.UserSubscriptionRepository;
import com.shokoku.streamfix.subscription.SubscriptionType;
import com.shokoku.streamfix.subscription.UserSubscription;
import com.shokoku.streamfix.user.UserPortResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

  @InjectMocks UserRepository sut;

  @Mock UserJpaRepository userJpaRepository;
  @Mock SocialUserJpaRepository socialUserJpaRepository;
  @Mock UserSubscriptionRepository userSubscriptionRepository;

  @Nested
  @DisplayName("findByEmail: 이메일로 사용자 조회")
  class FindByEmail {

    @DisplayName("실패: DB 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(userJpaRepository.findByEmail(DEFAULT_EMAIL))
          .thenThrow(new RuntimeException(aDbQueryErrorMessage()));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findByEmail(DEFAULT_EMAIL));
    }

    @DisplayName("성공: 존재하지 않는 이메일로 조회하면 Empty Optional을 반환한다")
    @Test
    void test1000() {
      // given
      String nonExistentEmail = aNonExistentEmail();
      when(userJpaRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

      // when
      Optional<UserPortResponse> result = sut.findByEmail(nonExistentEmail);

      // then
      assertTrue(result.isEmpty());
      verify(userJpaRepository).findByEmail(nonExistentEmail);
    }

    @DisplayName("성공: 존재하는 이메일로 조회하면 UserPortResponse를 반환한다")
    @Test
    void test1001() {
      // given
      UserEntity userEntity = aUserEntity();
      when(userJpaRepository.findByEmail(DEFAULT_EMAIL)).thenReturn(Optional.of(userEntity));

      // when
      Optional<UserPortResponse> result = sut.findByEmail(DEFAULT_EMAIL);

      // then
      assertTrue(result.isPresent());
      UserPortResponse response = result.get();
      assertEquals(userEntity.getUserId(), response.userId());
      assertEquals(userEntity.getPassword(), response.password());
      assertEquals(userEntity.getEmail(), response.username());
      assertEquals(userEntity.getEmail(), response.email());
      assertEquals(userEntity.getPhone(), response.phone());
      verify(userJpaRepository).findByEmail(DEFAULT_EMAIL);
    }

    @DisplayName("성공: 다른 사용자의 이메일로 조회할 수 있다")
    @Test
    void test1002() {
      // given
      UserEntity customUserEntity = aCustomUserEntity();
      when(userJpaRepository.findByEmail(CUSTOM_EMAIL)).thenReturn(Optional.of(customUserEntity));

      // when
      Optional<UserPortResponse> result = sut.findByEmail(CUSTOM_EMAIL);

      // then
      assertTrue(result.isPresent());
      UserPortResponse response = result.get();
      assertEquals(customUserEntity.getUserId(), response.userId());
      assertEquals(customUserEntity.getPassword(), response.password());
      assertEquals(customUserEntity.getEmail(), response.email());
      verify(userJpaRepository).findByEmail(CUSTOM_EMAIL);
    }
  }

  @Nested
  @DisplayName("findByProviderId: Provider ID로 소셜 사용자 조회")
  class FindByProviderId {

    @DisplayName("실패: DB 조회 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(socialUserJpaRepository.findByProviderId(DEFAULT_PROVIDER_ID))
          .thenThrow(new RuntimeException(aDbQueryErrorMessage()));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.findByProviderId(DEFAULT_PROVIDER_ID));
    }

    @DisplayName("성공: 존재하지 않는 Provider ID로 조회하면 Empty Optional을 반환한다")
    @Test
    void test1000() {
      // given
      String nonExistentProviderId = aNonExistentProviderId();
      when(socialUserJpaRepository.findByProviderId(nonExistentProviderId))
          .thenReturn(Optional.empty());

      // when
      Optional<UserPortResponse> result = sut.findByProviderId(nonExistentProviderId);

      // then
      assertTrue(result.isEmpty());
      verify(socialUserJpaRepository).findByProviderId(nonExistentProviderId);
    }

    @DisplayName("성공: 존재하는 Provider ID로 조회하고 구독 정보가 없으면 기본 구독을 생성한다")
    @Test
    void test1001() {
      // given
      SocialUserEntity socialUserEntity = aSocialUserEntity();
      when(socialUserJpaRepository.findByProviderId(DEFAULT_PROVIDER_ID))
          .thenReturn(Optional.of(socialUserEntity));
      when(userSubscriptionRepository.findByUserId(socialUserEntity.getSocialUserId()))
          .thenReturn(Optional.empty());

      // when
      Optional<UserPortResponse> result = sut.findByProviderId(DEFAULT_PROVIDER_ID);

      // then
      assertTrue(result.isPresent());
      UserPortResponse response = result.get();
      assertEquals(socialUserEntity.getSocialUserId(), response.userId());
      assertEquals(socialUserEntity.getProviderId(), response.providerId());
      assertEquals(socialUserEntity.getProvider(), response.provider());
      assertEquals(socialUserEntity.getUserName(), response.username());
      assertNotNull(response.role());
      verify(socialUserJpaRepository).findByProviderId(DEFAULT_PROVIDER_ID);
      verify(userSubscriptionRepository).findByUserId(socialUserEntity.getSocialUserId());
    }

    @DisplayName("성공: 존재하는 Provider ID로 조회하고 구독 정보가 있으면 해당 역할을 반환한다")
    @Test
    void test1002() {
      // given
      SocialUserEntity socialUserEntity = aSocialUserEntity();
      UserSubscription userSubscription = mock(UserSubscription.class);

      when(socialUserJpaRepository.findByProviderId(DEFAULT_PROVIDER_ID))
          .thenReturn(Optional.of(socialUserEntity));
      when(userSubscriptionRepository.findByUserId(socialUserEntity.getSocialUserId()))
          .thenReturn(Optional.of(userSubscription));
      when(userSubscription.getSubscriptionType()).thenReturn(SubscriptionType.GOLD);

      // when
      Optional<UserPortResponse> result = sut.findByProviderId(DEFAULT_PROVIDER_ID);

      // then
      assertTrue(result.isPresent());
      UserPortResponse response = result.get();
      assertEquals(socialUserEntity.getSocialUserId(), response.userId());
      assertEquals("ROLE_GOLD", response.role());
      verify(socialUserJpaRepository).findByProviderId(DEFAULT_PROVIDER_ID);
      verify(userSubscriptionRepository).findByUserId(socialUserEntity.getSocialUserId());
    }
  }

  @Nested
  @DisplayName("create: 새로운 일반 사용자 생성")
  class CreateUserTest {

    @DisplayName("실패: DB 저장 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      com.shokoku.streamfix.user.CreateUser createUser = aCreateUser();
      when(userJpaRepository.save(any(UserEntity.class)))
          .thenThrow(new RuntimeException(aDbSaveErrorMessage()));

      // when & then
      assertThrows(RuntimeException.class, () -> sut.create(createUser));
    }

    @DisplayName("실패: 구독 생성 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      com.shokoku.streamfix.user.CreateUser createUser = aCreateUser();
      UserEntity savedUser = aUserEntity();
      when(userJpaRepository.save(any(UserEntity.class))).thenReturn(savedUser);
      doThrow(new RuntimeException(aDbSaveErrorMessage()))
          .when(userSubscriptionRepository)
          .create(savedUser.getUserId());

      // when & then
      assertThrows(RuntimeException.class, () -> sut.create(createUser));
    }

    @DisplayName("성공: 유효한 파라미터로 사용자를 생성하고 UserPortResponse를 반환한다")
    @Test
    void test1000() {
      // given
      com.shokoku.streamfix.user.CreateUser createUser = aCreateUser();
      UserEntity savedUser = aUserEntity();
      when(userJpaRepository.save(any(UserEntity.class))).thenReturn(savedUser);

      // when
      UserPortResponse result = sut.create(createUser);

      // then
      assertNotNull(result);
      assertEquals(savedUser.getUserId(), result.userId());
      assertEquals(savedUser.getUserName(), result.username());
      assertEquals(savedUser.getPassword(), result.password());
      assertEquals(savedUser.getEmail(), result.email());
      assertEquals(savedUser.getPhone(), result.phone());
      verify(userJpaRepository).save(any(UserEntity.class));
      verify(userSubscriptionRepository).create(any(String.class));
    }
  }

  @Nested
  @DisplayName("createSocialUser: 새로운 소셜 사용자 생성")
  class CreateSocialUserTest {

    @DisplayName("실패: DB 저장 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test1() {
      // given
      when(socialUserJpaRepository.save(any(SocialUserEntity.class)))
          .thenThrow(new RuntimeException(aDbSaveErrorMessage()));

      // when & then
      assertThrows(
          RuntimeException.class,
          () ->
              sut.createSocialUser(DEFAULT_SOCIAL_USERNAME, DEFAULT_PROVIDER, DEFAULT_PROVIDER_ID));
    }

    @DisplayName("실패: 구독 생성 중 오류가 발생하면 관련 Exception을 던진다")
    @Test
    void test2() {
      // given
      SocialUserEntity savedSocialUser = aSocialUserEntity();
      when(socialUserJpaRepository.save(any(SocialUserEntity.class))).thenReturn(savedSocialUser);
      doThrow(new RuntimeException(aDbSaveErrorMessage()))
          .when(userSubscriptionRepository)
          .create(savedSocialUser.getSocialUserId());

      // when & then
      assertThrows(
          RuntimeException.class,
          () ->
              sut.createSocialUser(DEFAULT_SOCIAL_USERNAME, DEFAULT_PROVIDER, DEFAULT_PROVIDER_ID));
    }

    @DisplayName("성공: 유효한 파라미터로 소셜 사용자를 생성하고 UserPortResponse를 반환한다")
    @Test
    void test1000() {
      // given
      SocialUserEntity savedSocialUser = aSocialUserEntity();
      when(socialUserJpaRepository.save(any(SocialUserEntity.class))).thenReturn(savedSocialUser);

      // when
      UserPortResponse result =
          sut.createSocialUser(DEFAULT_SOCIAL_USERNAME, DEFAULT_PROVIDER, DEFAULT_PROVIDER_ID);

      // then
      assertNotNull(result);
      assertEquals(savedSocialUser.getProviderId(), result.providerId());
      assertEquals(savedSocialUser.getProvider(), result.provider());
      assertEquals(savedSocialUser.getUserName(), result.username());
      verify(socialUserJpaRepository).save(any(SocialUserEntity.class));
      verify(userSubscriptionRepository).create(any(String.class));
    }
  }

  @Nested
  @DisplayName("Port 인터페이스 구현 검증")
  class PortInterfaceImplementation {

    @DisplayName("FetchUserPort 인터페이스를 구현한다")
    @Test
    void implementsFetchUserPort() {
      assertInstanceOf(com.shokoku.streamfix.user.FetchUserPort.class, sut);
    }

    @DisplayName("InsertUserPort 인터페이스를 구현한다")
    @Test
    void implementsInsertUserPort() {
      assertInstanceOf(com.shokoku.streamfix.user.InsertUserPort.class, sut);
    }
  }

  @Nested
  @DisplayName("Transaction 동작 검증")
  class TransactionBehavior {

    @DisplayName("findByEmail 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void findByEmailMethodHasTransactionalAnnotation() {
      try {
        var method = UserRepository.class.getMethod("findByEmail", String.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("findByEmail 메소드를 찾을 수 없습니다");
      }
    }

    @DisplayName("create 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void createMethodHasTransactionalAnnotation() {
      try {
        var method =
            UserRepository.class.getMethod("create", com.shokoku.streamfix.user.CreateUser.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("create 메소드를 찾을 수 없습니다");
      }
    }

    @DisplayName("createSocialUser 메소드가 @Transactional 어노테이션을 가지고 있다")
    @Test
    void createSocialUserMethodHasTransactionalAnnotation() {
      try {
        var method =
            UserRepository.class.getMethod(
                "createSocialUser", String.class, String.class, String.class);
        assertTrue(
            method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));
      } catch (NoSuchMethodException e) {
        fail("createSocialUser 메소드를 찾을 수 없습니다");
      }
    }
  }

  @Nested
  @DisplayName("UserEntity Fixtures: 다양한 사용자 엔티티 생성 테스트")
  class UserEntityFixtures {

    @DisplayName("기본 사용자 엔티티를 생성할 수 있다")
    @Test
    void test1() {
      // when
      UserEntity entity = aUserEntity();

      // then
      assertEquals(DEFAULT_USERNAME, entity.getUserName());
      assertEquals(DEFAULT_PASSWORD, entity.getPassword());
      assertEquals(DEFAULT_EMAIL, entity.getEmail());
      assertEquals(DEFAULT_PHONE, entity.getPhone());
      assertNotNull(entity.getUserId());
    }

    @DisplayName("커스텀 사용자 엔티티를 생성할 수 있다")
    @Test
    void test2() {
      // when
      UserEntity entity = aCustomUserEntity();

      // then
      assertEquals(CUSTOM_USERNAME, entity.getUserName());
      assertEquals(CUSTOM_PASSWORD, entity.getPassword());
      assertEquals(CUSTOM_EMAIL, entity.getEmail());
      assertEquals(CUSTOM_PHONE, entity.getPhone());
    }

    @DisplayName("특정 파라미터로 사용자 엔티티를 생성할 수 있다")
    @Test
    void test3() {
      // when
      UserEntity entity =
          aUserEntityWith(UPDATE_USERNAME, UPDATE_PASSWORD, UPDATE_EMAIL, UPDATE_PHONE);

      // then
      assertEquals(UPDATE_USERNAME, entity.getUserName());
      assertEquals(UPDATE_PASSWORD, entity.getPassword());
      assertEquals(UPDATE_EMAIL, entity.getEmail());
      assertEquals(UPDATE_PHONE, entity.getPhone());
    }

    @DisplayName("다른 사용자 엔티티를 생성할 수 있다")
    @Test
    void test4() {
      // when
      UserEntity entity = anotherUserEntity();

      // then
      assertEquals(ANOTHER_USERNAME, entity.getUserName());
      assertEquals(ANOTHER_PASSWORD, entity.getPassword());
      assertEquals(ANOTHER_EMAIL, entity.getEmail());
      assertEquals(ANOTHER_PHONE, entity.getPhone());
    }

    @DisplayName("업데이트용 사용자 엔티티를 생성할 수 있다")
    @Test
    void test5() {
      // when
      UserEntity entity = anUpdatedUserEntity();

      // then
      assertEquals(UPDATE_USERNAME, entity.getUserName());
      assertEquals(UPDATE_PASSWORD, entity.getPassword());
      assertEquals(UPDATE_EMAIL, entity.getEmail());
      assertEquals(UPDATE_PHONE, entity.getPhone());
    }

    @DisplayName("기본 소셜 사용자 엔티티를 생성할 수 있다")
    @Test
    void test6() {
      // when
      SocialUserEntity entity = aSocialUserEntity();

      // then
      assertEquals(DEFAULT_SOCIAL_USERNAME, entity.getUserName());
      assertEquals(DEFAULT_PROVIDER, entity.getProvider());
      assertEquals(DEFAULT_PROVIDER_ID, entity.getProviderId());
      assertNotNull(entity.getSocialUserId());
    }

    @DisplayName("커스텀 소셜 사용자 엔티티를 생성할 수 있다")
    @Test
    void test7() {
      // when
      SocialUserEntity entity = aCustomSocialUserEntity();

      // then
      assertEquals(CUSTOM_SOCIAL_USERNAME, entity.getUserName());
      assertEquals(CUSTOM_PROVIDER, entity.getProvider());
      assertEquals(CUSTOM_PROVIDER_ID, entity.getProviderId());
    }

    @DisplayName("특정 파라미터로 소셜 사용자 엔티티를 생성할 수 있다")
    @Test
    void test8() {
      // when
      SocialUserEntity entity =
          aSocialUserEntityWith(ANOTHER_SOCIAL_USERNAME, ANOTHER_PROVIDER, ANOTHER_PROVIDER_ID);

      // then
      assertEquals(ANOTHER_SOCIAL_USERNAME, entity.getUserName());
      assertEquals(ANOTHER_PROVIDER, entity.getProvider());
      assertEquals(ANOTHER_PROVIDER_ID, entity.getProviderId());
    }

    @DisplayName("다른 소셜 사용자 엔티티를 생성할 수 있다")
    @Test
    void test9() {
      // when
      SocialUserEntity entity = anotherSocialUserEntity();

      // then
      assertEquals(ANOTHER_SOCIAL_USERNAME, entity.getUserName());
      assertEquals(ANOTHER_PROVIDER, entity.getProvider());
      assertEquals(ANOTHER_PROVIDER_ID, entity.getProviderId());
    }

    @DisplayName("CreateUser 객체를 생성할 수 있다")
    @Test
    void test10() {
      // when
      com.shokoku.streamfix.user.CreateUser createUser = aCreateUser();

      // then
      assertEquals(DEFAULT_USERNAME, createUser.username());
      assertEquals(DEFAULT_PASSWORD, createUser.encryptedPassword());
      assertEquals(DEFAULT_EMAIL, createUser.email());
      assertEquals(DEFAULT_PHONE, createUser.phone());
    }

    @DisplayName("커스텀 CreateUser 객체를 생성할 수 있다")
    @Test
    void test11() {
      // when
      com.shokoku.streamfix.user.CreateUser createUser = aCustomCreateUser();

      // then
      assertEquals(CUSTOM_USERNAME, createUser.username());
      assertEquals(CUSTOM_PASSWORD, createUser.encryptedPassword());
      assertEquals(CUSTOM_EMAIL, createUser.email());
      assertEquals(CUSTOM_PHONE, createUser.phone());
    }

    @DisplayName("특정 파라미터로 CreateUser 객체를 생성할 수 있다")
    @Test
    void test12() {
      // when
      com.shokoku.streamfix.user.CreateUser createUser =
          aCreateUserWith(UPDATE_USERNAME, UPDATE_PASSWORD, UPDATE_EMAIL, UPDATE_PHONE);

      // then
      assertEquals(UPDATE_USERNAME, createUser.username());
      assertEquals(UPDATE_PASSWORD, createUser.encryptedPassword());
      assertEquals(UPDATE_EMAIL, createUser.email());
      assertEquals(UPDATE_PHONE, createUser.phone());
    }

    @DisplayName("존재하지 않는 데이터를 제공할 수 있다")
    @Test
    void test13() {
      // when
      String nonExistentUserId = aNonExistentUserId();
      String nonExistentEmail = aNonExistentEmail();
      String nonExistentProviderId = aNonExistentProviderId();

      // then
      assertEquals(NON_EXISTENT_USER_ID, nonExistentUserId);
      assertEquals(NON_EXISTENT_EMAIL, nonExistentEmail);
      assertEquals(NON_EXISTENT_PROVIDER_ID, nonExistentProviderId);
    }

    @DisplayName("에러 메시지를 제공할 수 있다")
    @Test
    void test14() {
      // when
      String dbSaveError = aDbSaveErrorMessage();
      String dbQueryError = aDbQueryErrorMessage();
      String userNotFoundError = aUserNotFoundErrorMessage();
      String emailDuplicateError = anEmailDuplicateErrorMessage();

      // then
      assertEquals(DB_SAVE_ERROR_MESSAGE, dbSaveError);
      assertEquals(DB_QUERY_ERROR_MESSAGE, dbQueryError);
      assertEquals(USER_NOT_FOUND_ERROR_MESSAGE, userNotFoundError);
      assertEquals(EMAIL_DUPLICATE_ERROR_MESSAGE, emailDuplicateError);
    }
  }

  @Nested
  @DisplayName("UserPortResponse Fixtures: 다양한 사용자 응답 생성 테스트")
  class UserPortResponseFixtures {

    @DisplayName("기본 사용자 포트 응답을 생성할 수 있다")
    @Test
    void test1() {
      // when
      UserPortResponse response = aUserPortResponse();

      // then
      assertEquals(DEFAULT_USER_ID, response.userId());
      assertEquals(DEFAULT_USERNAME, response.username());
      assertEquals(DEFAULT_PASSWORD, response.password());
      assertEquals(DEFAULT_EMAIL, response.email());
      assertEquals(DEFAULT_PHONE, response.phone());
    }

    @DisplayName("커스텀 사용자 응답을 생성할 수 있다")
    @Test
    void test2() {
      // when
      UserPortResponse response = aCustomUserPortResponse();

      // then
      assertEquals(CUSTOM_USER_ID, response.userId());
      assertEquals(CUSTOM_USERNAME, response.username());
      assertEquals(CUSTOM_PASSWORD, response.password());
      assertEquals(CUSTOM_EMAIL, response.email());
      assertEquals(CUSTOM_PHONE, response.phone());
    }

    @DisplayName("소셜 사용자 응답을 생성할 수 있다")
    @Test
    void test3() {
      // when
      UserPortResponse response = aSocialUserPortResponse();

      // then
      assertEquals(DEFAULT_SOCIAL_USER_ID, response.userId());
      assertEquals(DEFAULT_SOCIAL_USERNAME, response.username());
      assertEquals(DEFAULT_PROVIDER, response.provider());
      assertEquals(DEFAULT_PROVIDER_ID, response.providerId());
      assertEquals(DEFAULT_ROLE, response.role());
    }

    @DisplayName("특정 역할을 가진 사용자 응답을 생성할 수 있다")
    @Test
    void test4() {
      // when
      UserPortResponse adminResponse = aUserPortResponseWithRole(ADMIN_ROLE);
      UserPortResponse goldResponse = aUserPortResponseWithRole(GOLD_ROLE);

      // then
      assertEquals(ADMIN_ROLE, adminResponse.role());
      assertEquals(GOLD_ROLE, goldResponse.role());
    }

    @DisplayName("특정 이메일을 가진 사용자 응답을 생성할 수 있다")
    @Test
    void test5() {
      // when
      UserPortResponse response = aUserPortResponseWithEmail(CUSTOM_EMAIL);

      // then
      assertEquals(CUSTOM_EMAIL, response.email());
      assertEquals(DEFAULT_USERNAME, response.username());
    }

    @DisplayName("특정 Provider ID를 가진 소셜 사용자 응답을 생성할 수 있다")
    @Test
    void test6() {
      // when
      UserPortResponse response = aSocialUserPortResponseWithProviderId(CUSTOM_PROVIDER_ID);

      // then
      assertEquals(CUSTOM_PROVIDER_ID, response.providerId());
      assertEquals(DEFAULT_SOCIAL_USERNAME, response.username());
      assertEquals(DEFAULT_PROVIDER, response.provider());
      assertEquals(DEFAULT_ROLE, response.role());
    }

    @DisplayName("관리자 사용자 응답을 생성할 수 있다")
    @Test
    void test7() {
      // when
      UserPortResponse response = anAdminUserPortResponse();

      // then
      assertEquals(ADMIN_ROLE, response.role());
      assertEquals(DEFAULT_USER_ID, response.userId());
      assertEquals(DEFAULT_USERNAME, response.username());
    }

    @DisplayName("골드 사용자 응답을 생성할 수 있다")
    @Test
    void test8() {
      // when
      UserPortResponse response = aGoldUserPortResponse();

      // then
      assertEquals(GOLD_ROLE, response.role());
      assertEquals(DEFAULT_USER_ID, response.userId());
      assertEquals(DEFAULT_USERNAME, response.username());
    }
  }
}
