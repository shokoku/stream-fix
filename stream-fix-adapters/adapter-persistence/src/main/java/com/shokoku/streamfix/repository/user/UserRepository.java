package com.shokoku.streamfix.repository.user;

import com.shokoku.streamfix.entity.user.SocialUserEntity;
import com.shokoku.streamfix.entity.user.UserEntity;
import com.shokoku.streamfix.repository.subscription.UserSubscriptionRepository;
import com.shokoku.streamfix.subscription.UserSubscription;
import com.shokoku.streamfix.user.CreateUser;
import com.shokoku.streamfix.user.FetchUserPort;
import com.shokoku.streamfix.user.InsertUserPort;
import com.shokoku.streamfix.user.UserPortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements FetchUserPort, InsertUserPort {

  private final UserJpaRepository userJpaRepository;
  private final SocialUserJpaRepository socialUserJpaRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;

  @Override
  @Transactional
  public Optional<UserPortResponse> findByEmail(String email) {
    Optional<UserEntity> byEmail = userJpaRepository.findByEmail(email);

    return byEmail.map(
        userEntity ->
            UserPortResponse.builder()
                .userId(userEntity.getUserId())
                .password(userEntity.getPassword())
                .username(userEntity.getEmail())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build());
  }

  @Override
  public Optional<UserPortResponse> findByProviderId(String providerId) {
    Optional<SocialUserEntity> byProviderId = socialUserJpaRepository.findByProviderId(providerId);
    if (byProviderId.isEmpty()) {
      return Optional.empty();
    }

    SocialUserEntity socialUserEntity = byProviderId.get();

    Optional<UserSubscription> byUserId = userSubscriptionRepository.findByUserId(
        socialUserEntity.getSocialUserId());

    return Optional.of(
        UserPortResponse.builder()
            .providerId(socialUserEntity.getProviderId())
            .provider(socialUserEntity.getProvider())
            .username(socialUserEntity.getUserName())
            .role(byUserId.orElse(UserSubscription.newSubscription(socialUserEntity.getSocialUserId())).getSubscriptionType().toRole())
            .build());
  }

  @Override
  @Transactional
  public UserPortResponse create(CreateUser user) {
    UserEntity userEntity =
        new UserEntity(user.username(), user.encryptedPassword(), user.email(), user.phone());
    UserEntity save = userJpaRepository.save(userEntity);

    userSubscriptionRepository.create(userEntity.getUserId());

    return UserPortResponse.builder()
        .userId(save.getUserId())
        .username(save.getUserName())
        .password(save.getPassword())
        .email(save.getEmail())
        .phone(save.getPhone())
        .build();
  }

  @Override
  @Transactional
  public UserPortResponse createSocialUser(String username, String provider, String providerId) {
    SocialUserEntity socialUserEntity = new SocialUserEntity(username, provider, providerId);
    socialUserJpaRepository.save(socialUserEntity);

    userSubscriptionRepository.create(socialUserEntity.getSocialUserId());

    return UserPortResponse.builder()
        .providerId(socialUserEntity.getProviderId())
        .provider(socialUserEntity.getProvider())
        .username(socialUserEntity.getUserName())
        .build();
  }
}
