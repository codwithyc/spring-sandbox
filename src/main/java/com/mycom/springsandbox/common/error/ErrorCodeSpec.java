package com.mycom.springsandbox.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCodeSpec {
    HttpStatus status();
    String code();
    String messageKey();
    String defaultMessage();
}
