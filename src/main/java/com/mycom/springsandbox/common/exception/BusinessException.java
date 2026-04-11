package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class BusinessException extends RuntimeException {

  private final ErrorCodeSpec errorCode;

  public BusinessException(ErrorCodeSpec errorCode) {
    super(errorCode.defaultMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCodeSpec errorCode, String detailMessage) {
    super(detailMessage);
    this.errorCode = errorCode;
  }

  public ErrorCodeSpec getErrorCode() {
    return errorCode;
  }
}

