package com.mycom.springsandbox.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mycom.springsandbox.common.error.ApiError;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiEnvelope<T>(
        boolean success,
        T data,
        ApiError error,
        ApiMeta meta
) {

    public ApiEnvelope{
        if(success && error != null){
            throw new IllegalArgumentException("success=true 인 경우 error는 null이어야 합니다.");
        }
        if(!success && data != null){
            throw new IllegalArgumentException("success=false 인 경우 data는 null이어야 합니다.");
        }
    }

    public static <T> ApiEnvelope<T> ok(T data, ApiMeta meta){
        return new ApiEnvelope<>(true, data, null, meta);
    }

    public static <T> ApiEnvelope<T> fail(ApiError error, ApiMeta meta){
        return new ApiEnvelope<>(false, null, error, meta);
    }
}
