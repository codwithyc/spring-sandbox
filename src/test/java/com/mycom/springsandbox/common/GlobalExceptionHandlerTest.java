package com.mycom.springsandbox.common;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.exception.BusinessException;
import com.mycom.springsandbox.common.handler.GlobalExceptionHandler;
import com.mycom.springsandbox.common.error.ApiError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    @DisplayName("비지니스 예외 헨들링 테스트")
    void handleBusinessException() {
        // Given

        // When

        // Then

    }

}
