package com.shokoku.streamfix.authcation;

import lombok.Getter;

@Getter
public class RequestedBy implements Authentication {

  private final String requestBy;

  public RequestedBy(String requestBy) {
    this.requestBy = requestBy;
  }
}
