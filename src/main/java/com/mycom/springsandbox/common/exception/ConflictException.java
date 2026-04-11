package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class ConflictException extends BusinessException {
  public ConflictException() {
    super(ErrorCode.CONFLICT);
  }

  public ConflictException(String detailMessage) {
    super(ErrorCode.CONFLICT, detailMessage);
  }

  protected ConflictException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
