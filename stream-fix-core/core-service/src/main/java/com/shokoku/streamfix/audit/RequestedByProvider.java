package com.shokoku.streamfix.audit;

import java.util.Optional;

public interface RequestedByProvider {
  Optional<String> getRequestedBy();
}
