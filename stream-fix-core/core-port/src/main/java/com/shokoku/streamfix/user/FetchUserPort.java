package com.shokoku.streamfix.user;

import java.util.Optional;

public interface FetchUserPort {
  Optional<UserPortResponse> findByEmail(String email);
}
