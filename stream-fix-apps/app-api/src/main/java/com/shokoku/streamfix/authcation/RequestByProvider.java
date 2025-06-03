package com.shokoku.streamfix.authcation;

import java.util.Optional;

public interface RequestByProvider {

  Optional<String> getRequestedBy();

}
