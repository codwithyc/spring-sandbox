package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;

public class NotFoundException extends BusinessException {
  public NotFoundException(String detailMessage) {
    super(ErrorCode.NOT_FOUND, detailMessage);
  }

  public NotFoundException(String resourceName, Object identifier) {
    this(String.format("%s(%s)를 찾을 수 없습니다.", resourceName, identifier));
  }

  protected NotFoundException(ErrorCodeSpec errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}
