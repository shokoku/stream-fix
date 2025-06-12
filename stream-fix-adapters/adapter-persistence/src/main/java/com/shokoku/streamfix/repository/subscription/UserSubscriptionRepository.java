package com.shokoku.streamfix.repository.subscription;

import com.shokoku.streamfix.entity.subscription.UserSubscriptionEntity;
import com.shokoku.streamfix.subscription.FetchUserSubscriptionPort;
import com.shokoku.streamfix.subscription.InsertUserSubscriptionPort;
import com.shokoku.streamfix.subscription.UpdateUserSubscriptionPort;
import com.shokoku.streamfix.subscription.UserSubscription;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserSubscriptionRepository
    implements FetchUserSubscriptionPort, UpdateUserSubscriptionPort, InsertUserSubscriptionPort {

  private final UserSubscriptionJpaRepository userSubscriptionJpaRepository;

  @Override
  @Transactional
  public Optional<UserSubscription> findByUserId(String userId) {
    return userSubscriptionJpaRepository.findByUserId(userId).map(UserSubscriptionEntity::toDomain);
  }

  @Override
  @Transactional
  public void create(String userId) {
    UserSubscription userSubscription = UserSubscription.newSubscription(userId);
    userSubscriptionJpaRepository.save(UserSubscriptionEntity.toEntity(userSubscription));
  }

  @Override
  @Transactional
  public void update(UserSubscription userSubscription) {
    userSubscriptionJpaRepository.save(UserSubscriptionEntity.toEntity(userSubscription));
  }
}
