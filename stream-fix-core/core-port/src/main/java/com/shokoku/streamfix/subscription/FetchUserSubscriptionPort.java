package com.shokoku.streamfix.subscription;

import java.util.Optional;

public interface FetchUserSubscriptionPort {

  Optional<UserSubscription> findByUserId(String userId);
}
