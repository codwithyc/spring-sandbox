package com.mycom.springsandbox.common.error;

import java.util.List;

public record ApiError(
        String code,
        int status,
        String message,
        String messageKey,
        List<FieldErrorItem> fieldErrors,
        String traceId
) {
    public ApiError {
        fieldErrors = (fieldErrors == null) ? List.of() : List.copyOf(fieldErrors);
    }

    public static ApiError of(
            ErrorCodeSpec code,
            String message,
            List<FieldErrorItem> fieldErrors,
            String traceId
    ) {
        String resolvedMessage = (message == null || message.isBlank()) ? code.defaultMessage() : message;

        return new ApiError(
                code.code(),
                code.status().value(),
                resolvedMessage,
                code.messageKey(),
                fieldErrors,
                traceId
        );
    }
}

