package com.mycom.springsandbox.common.exception;

import com.mycom.springsandbox.common.enums.ErrorCode;

public class NotFoundException extends BusinessException {
  public NotFoundException(String resourceName, Object identifier) {
    super(
            ErrorCode.NOT_FOUND,  // 여기에 정확히 ErrorCode를 넘기고
            String.format("%s(%s)를 찾을 수 없습니다.", resourceName, identifier)
    );
  }
}
