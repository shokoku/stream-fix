package com.shokoku.streamfix.user;

public interface InsertUserPort {
  UserPortResponse create(CreateUser user);
}
