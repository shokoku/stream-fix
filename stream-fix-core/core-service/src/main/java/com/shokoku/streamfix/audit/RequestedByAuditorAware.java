package com.shokoku.streamfix.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RequestedByAuditorAware implements AuditorAware<String> {
  private final ApplicationContext context;

  @Override
  public Optional<String> getCurrentAuditor() {
    try {
      return Optional.of(context.getBean(RequestedByProvider.class))
          .flatMap(RequestedByProvider::getRequestedBy);

    } catch (Exception e) {
      return Optional.of("system");
    }
  }
}
