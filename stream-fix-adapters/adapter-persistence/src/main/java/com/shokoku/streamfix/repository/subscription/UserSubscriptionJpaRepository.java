package com.shokoku.streamfix.repository.subscription;

import com.shokoku.streamfix.entity.subscription.UserSubscriptionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionJpaRepository
    extends JpaRepository<UserSubscriptionEntity, String> {

  Optional<UserSubscriptionEntity> findByUserId(String userId);
}
