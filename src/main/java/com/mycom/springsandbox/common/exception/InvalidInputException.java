package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class InvalidInputException extends BusinessException {
  public InvalidInputException() {
    super(ErrorCode.INVALID_INPUT);
  }

  public InvalidInputException(String detailMessage) {
    super(ErrorCode.INVALID_INPUT, detailMessage);
  }

  protected InvalidInputException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
