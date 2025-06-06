package com.shokoku.streamfix.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryService implements LogUserAuditHistoryCase {

  private final UserHistoryPort userHistoryPort;

  @Override
  public void log(
      String userId,
      String userRole,
      String clientIp,
      String reqMethod,
      String reqUrl,
      String reqHeader,
      String reqPayload) {
    userHistoryPort.create(userId, userRole, clientIp, reqMethod, reqUrl, reqHeader, reqPayload);
  }
}
