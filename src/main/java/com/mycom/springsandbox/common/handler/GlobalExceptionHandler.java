package com.mycom.springsandbox.common.handler;

import com.mycom.springsandbox.common.api.ApiEnvelope;
import com.mycom.springsandbox.common.api.ApiEnvelopes;
import com.mycom.springsandbox.common.error.ApiError;
import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;
import com.mycom.springsandbox.common.error.FieldErrorItem;
import com.mycom.springsandbox.common.exception.BusinessException;
import com.mycom.springsandbox.common.web.RequestIds;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ApiEnvelopes envelopes;

    public GlobalExceptionHandler(ApiEnvelopes envelopes) {
        this.envelopes = envelopes;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCodeSpec code = ex.getErrorCode();
        ApiError error = ApiError.of(code, ex.getMessage(), List.of(), traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorItem> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorItem)
                .toList();

        ErrorCodeSpec code = ErrorCode.VALIDATION_ERROR;
        ApiError error = ApiError.of(code, code.defaultMessage(), fieldErrors, traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorItem> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> new FieldErrorItem(v.getPropertyPath().toString(), "CONSTRAINT", v.getMessage()))
                .toList();

        ErrorCodeSpec code = ErrorCode.VALIDATION_ERROR;
        ApiError error = ApiError.of(code, code.defaultMessage(), fieldErrors, traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleBadJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ErrorCodeSpec code = ErrorCode.REQUEST_BODY_NOT_READABLE;
        ApiError error = ApiError.of(code, "요청 본문(JSON) 형식이 올바르지 않습니다.", List.of(), traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        ErrorCodeSpec code = ErrorCode.NOT_FOUND;
        ApiError error = ApiError.of(code, "존재하지 않는 API 입니다.", List.of(), traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ErrorCodeSpec code = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiError error = ApiError.of(code, code.defaultMessage(), List.of(), traceId(request));
        return envelopes.fail(code.status(), error, request);
    }

    private FieldErrorItem toFieldErrorItem(FieldError fe) {
        return new FieldErrorItem(fe.getField(), fe.getCode(), fe.getDefaultMessage());
    }

    private String traceId(HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        Object requestId = request.getAttribute(RequestIds.REQUEST_ID_ATTR);
        return requestId == null ? null : requestId.toString();
    }
}
