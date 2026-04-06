package com.mycom.springsandbox.common.enums;

import com.mycom.springsandbox.common.error.ErrorCodeSpec;
import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorCodeSpec {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COM-4001", "error.common.invalid_input", "입력값이 유효하지 않습니다."),
    REQUEST_BODY_NOT_READABLE(HttpStatus.BAD_REQUEST, "COM-4002", "error.common.request_body_not_readable", "요청 본문을 읽을 수 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COM-4003", "error.common.validation_error", "입력값 검증에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COM-4004", "error.common.unauthorized", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COM-4005", "error.common.access_denied", "접근이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COM-4006", "error.common.not_found", "대상을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COM-4007", "error.common.method_not_allowed", "허용되지 않은 HTTP 메서드입니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "COM-4008", "error.common.not_acceptable", "요청한 리소스는 요청한 형식으로 응답할 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "COM-4009", "error.common.conflict", "요청이 현재 상태와 충돌합니다."),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COM-4010", "error.common.unsupported_media_type", "지원되지 않는 미디어 타입입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM-5001", "error.common.internal_server_error", "서버 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "COM-5002", "error.common.service_unavailable", "서비스를 사용할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String messageKey;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String code, String messageKey, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageKey() {
        return messageKey;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
