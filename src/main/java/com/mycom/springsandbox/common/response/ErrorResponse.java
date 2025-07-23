package com.mycom.springsandbox.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String code,
        String message,
        String path,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    @Builder
    public ErrorResponse { }

    private static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // GlobalExceptionHandler에서 간편하게 호출하는 팩토리 메서드
    public static ErrorResponse of(int status, String code, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .path(path)
                .timestamp(now())
                .build();
    }
}
