package com.shokoku.streamfix.subscription;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSubscription {
  private String userId;
  private SubscriptionType subscriptionType;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private Boolean validYn;

  public void off() {
    this.validYn = false;
  }

  public void renew() {
    this.startAt = LocalDateTime.now();
    this.endAt = getEndAt(startAt);
    this.validYn = true;
  }

  public void change(SubscriptionType type) {
    this.subscriptionType = type;
  }

  public boolean ableToRenew() {
    LocalDateTime now = LocalDateTime.now();
    return now.isAfter(endAt);
  }

  public boolean ableToChange() {
    LocalDateTime now = LocalDateTime.now();
    return now.isBefore(endAt) && now.isAfter(startAt) && validYn;
  }

  public static UserSubscription newSubscription(String userId) {
    LocalDateTime now = LocalDateTime.now();
    return UserSubscription.builder()
        .userId(userId)
        .subscriptionType(SubscriptionType.FREE)
        .startAt(now)
        .endAt(getEndAt(now))
        .validYn(true)
        .build();
  }

  private static LocalDateTime getEndAt(LocalDateTime startAt) {
    return startAt.plus(Duration.ofDays(30));
  }
}
