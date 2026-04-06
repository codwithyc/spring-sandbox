package com.mycom.springsandbox.common.error;

public record FieldErrorItem(
        String field,
        String code,
        String message
) {
}
