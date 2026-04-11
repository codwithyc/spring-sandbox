package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class ValidationException extends BusinessException {
  public ValidationException(String detailMessage) {
    super(ErrorCode.VALIDATION_ERROR, detailMessage);
  }

  protected ValidationException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
