package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;

public class ValidationException extends RuntimeException {
  private final ErrorCode errorCode;

  public ValidationException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
