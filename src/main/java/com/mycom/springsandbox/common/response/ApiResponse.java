package com.mycom.springsandbox.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycom.springsandbox.common.enums.ErrorCode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.LocalDateTime.now;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        Integer errorCode,
        String message,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
    @Builder
    public  ApiResponse {
    }

    private static LocalDateTime getCurrentTimestamp() {
        return now();
    }

    // 성공 응답만 알릴 때
    public static <T> ApiResponse<T> success(String message) {
        Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(getCurrentTimestamp())
                .build();
    }

    // 데이터와 함께 성공 응답
    public static <T> ApiResponse<T> success(String message, T data) {
        Objects.requireNonNull(message, "메시지는 null일 수 없습니다.");
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(getCurrentTimestamp())
                .build();
    }
}