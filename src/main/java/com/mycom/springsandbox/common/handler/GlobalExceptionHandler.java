package com.mycom.springsandbox.common.handler;

import com.mycom.springsandbox.common.enums.ErrorCode;
import com.mycom.springsandbox.common.exception.BusinessException;
import com.mycom.springsandbox.common.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1) 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest req) {
        ErrorResponse body = ErrorResponse.of(
                ex.getErrorCode().getHttpStatus().value(),
                ex.getErrorCode().name(),
                ex.getErrorCode().getDefaultMessage(),
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(body);
    }

    // 2) @Valid / @RequestBody 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException ex, WebRequest req) {
        // 첫 번째 필드 에러 메시지만 예시로 뽑아봄
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_FAILED.name(),
                message,
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 3) URI 잘못 호출 (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex, WebRequest req) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                ErrorCode.NOT_FOUND.name(),
                "존재하지 않는 API 입니다.",
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 4) ConstraintViolation (path variable, request param 검증 실패)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest req) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .orElse("잘못된 요청입니다.");

        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.VALIDATION_FAILED.name(),
                message,
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 5) 나머지 알 수 없는 예외 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, WebRequest req) {
        log.error("Unhandled exception caught:", ex);
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "서버 오류가 발생했습니다.",
                req.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
