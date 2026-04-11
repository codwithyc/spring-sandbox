package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class UnauthorizedException extends BusinessException {
  public UnauthorizedException() {
    super(ErrorCode.UNAUTHORIZED);
  }

  public UnauthorizedException(String detailMessage) {
    super(ErrorCode.UNAUTHORIZED, detailMessage);
  }

  protected UnauthorizedException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
