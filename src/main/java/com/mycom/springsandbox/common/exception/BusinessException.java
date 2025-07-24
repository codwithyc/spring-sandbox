package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;

public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getDefaultMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String detailMessage) {
    super(detailMessage);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
