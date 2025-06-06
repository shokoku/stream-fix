package com.shokoku.streamfix.user;

public interface LogUserAuditHistoryCase {

  void log(String userId, String userRole, String clientIp, String reqMethod,
      String reqUrl, String reqHeader, String reqPayload);

}
