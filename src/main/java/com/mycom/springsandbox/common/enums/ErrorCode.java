package com.mycom.springsandbox.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용자 관련(1000 ~ 1999)
    USER_NOT_FOUND(1001, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1002, "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 인증/인가(2000~2999)
    TOKEN_EXPIRED(2001, "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(2002, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 공통 오류(9000~9999)
    NOT_FOUND(9001, "존재하지 않는 API 입니다.", HttpStatus.NOT_FOUND),
    VALIDATION_FAILED(9002, "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(9003, "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final int code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
