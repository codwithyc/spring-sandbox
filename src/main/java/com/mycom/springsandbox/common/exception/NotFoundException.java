package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;

public class NotFoundException extends BusinessException {
  public NotFoundException(String resourceName, Object identifier) {
    super(
            ErrorCode.NOT_FOUND,
            String.format("%s(%s)를 찾을 수 없습니다.", resourceName, identifier)
    );
  }
}
