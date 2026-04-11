package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class ServiceUnavailableException extends BusinessException {
  public ServiceUnavailableException() {
    super(ErrorCode.SERVICE_UNAVAILABLE);
  }

  public ServiceUnavailableException(String detailMessage) {
    super(ErrorCode.SERVICE_UNAVAILABLE, detailMessage);
  }

  protected ServiceUnavailableException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
