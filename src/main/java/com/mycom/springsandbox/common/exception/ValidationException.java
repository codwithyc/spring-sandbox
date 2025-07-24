package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;

public class ValidationException extends BusinessException {
  private final ErrorCode errorCode;

  public ValidationException(ErrorCode errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
  }

  @Override
  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
