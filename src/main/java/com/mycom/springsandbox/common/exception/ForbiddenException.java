package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class ForbiddenException extends BusinessException {
  public ForbiddenException() {
    super(ErrorCode.ACCESS_DENIED);
  }

  public ForbiddenException(String detailMessage) {
    super(ErrorCode.ACCESS_DENIED, detailMessage);
  }

  protected ForbiddenException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
