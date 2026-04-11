package com.mycom.springsandbox.common.handler;

import com.mycom.springsandbox.common.api.ApiEnvelope;
import com.mycom.springsandbox.common.api.ApiEnvelopes;
import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.error.ApiError;
import com.mycom.springsandbox.common.error.ErrorCodeSpec;
import com.mycom.springsandbox.common.error.FieldErrorItem;
import com.mycom.springsandbox.common.exception.BusinessException;
import com.mycom.springsandbox.common.web.RequestIds;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ApiEnvelopes envelopes;

    public GlobalExceptionHandler(ApiEnvelopes envelopes) {
        this.envelopes = envelopes;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleBusiness(BusinessException ex, HttpServletRequest request) {
        ErrorCodeSpec code = ex.getErrorCode();
        ApiError error = ApiError.of(code, ex.getMessage(), List.of(), requestId(request));
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

        ApiError error = ApiError.of(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.VALIDATION_ERROR.defaultMessage(),
                fieldErrors,
                requestId(request)
        );
        return envelopes.fail(ErrorCode.VALIDATION_ERROR.status(), error, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> handleUnknown(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ApiError error = ApiError.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.defaultMessage(),
                List.of(),
                requestId(request)
        );
        return envelopes.fail(ErrorCode.INTERNAL_SERVER_ERROR.status(), error, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<FieldErrorItem> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorItem)
                .toList();

        return frameworkFail(httpRequest(request), headers, ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.defaultMessage(), fieldErrors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return frameworkFail(
                httpRequest(request),
                headers,
                ErrorCode.REQUEST_BODY_NOT_READABLE,
                "요청 본문(JSON) 형식이 올바르지 않습니다.",
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        // 405는 URL은 매칭됐지만 HTTP method가 허용되지 않았다는 의미이므로 Allow 헤더를 보존한다.
        return frameworkFail(
                httpRequest(request),
                headers,
                ErrorCode.METHOD_NOT_ALLOWED,
                ErrorCode.METHOD_NOT_ALLOWED.defaultMessage(),
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return frameworkFail(
                httpRequest(request),
                headers,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.defaultMessage(),
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return frameworkFail(
                httpRequest(request),
                headers,
                ErrorCode.NOT_ACCEPTABLE,
                ErrorCode.NOT_ACCEPTABLE.defaultMessage(),
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String message = "%s 파라미터는 필수입니다.".formatted(ex.getParameterName());
        return frameworkFail(httpRequest(request), headers, ErrorCode.INVALID_INPUT, message, List.of());
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String propertyName = ex.getPropertyName() == null ? "요청 값" : ex.getPropertyName();
        String message = "%s 타입이 올바르지 않습니다.".formatted(propertyName);
        return frameworkFail(httpRequest(request), headers, ErrorCode.INVALID_INPUT, message, List.of());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return frameworkFail(httpRequest(request), headers, ErrorCode.NOT_FOUND, "존재하지 않는 API 입니다.", List.of());
    }

    private FieldErrorItem toFieldErrorItem(FieldError fe) {
        return new FieldErrorItem(fe.getField(), fe.getCode(), fe.getDefaultMessage());
    }

    private ResponseEntity<Object> frameworkFail(
            HttpServletRequest request,
            HttpHeaders headers,
            ErrorCodeSpec code,
            String message,
            List<FieldErrorItem> fieldErrors
    ) {
        ApiError error = ApiError.of(code, message, fieldErrors, requestId(request));
        ResponseEntity<ApiEnvelope<Void>> response = envelopes.fail(code.status(), error, request);
        HttpHeaders mergedHeaders = new HttpHeaders();
        mergedHeaders.addAll(headers);
        mergedHeaders.addAll(response.getHeaders());

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(mergedHeaders)
                .body(response.getBody());
    }

    private HttpServletRequest httpRequest(WebRequest request) {
        return ((ServletWebRequest) request).getRequest();
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIds.REQUEST_ID_ATTR);
        return requestId == null ? null : requestId.toString();
    }
}
