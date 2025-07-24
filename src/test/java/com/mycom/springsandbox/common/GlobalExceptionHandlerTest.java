package com.mycom.springsandbox.common;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.exception.BusinessException;
import com.mycom.springsandbox.common.handler.GlobalExceptionHandler;
import com.mycom.springsandbox.common.response.ErrorResponse;
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
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BusinessException businessException = new BusinessException(ErrorCode.USER_NOT_FOUND, "잘못된 요청입니다.");
        WebRequest req = mock(WebRequest.class);
        when(req.getDescription(false)).thenReturn("uri=/api/user/1");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusiness(businessException, req);

        // Then
        // 예외가 올바르게 처리되었는지 검증합니다.
        assertThat(response.getStatusCode().value()).isEqualTo(ErrorCode.USER_NOT_FOUND.getHttpStatus().value());
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
        assertThat(response.getBody().message()).isEqualTo(ErrorCode.USER_NOT_FOUND.getDefaultMessage());
        assertThat(response.getBody().path()).isEqualTo("/api/user/1");

    }

}
