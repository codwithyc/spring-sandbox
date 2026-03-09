# 🌐 예외 처리 시스템 (Global Exception Handling)

## 🎯 목적

* 공통적인 에러 응답 포맷을 통해 클라이언트와의 명확한 계약 제공
* 예외 발생 시 일관된 로깅 및 응답 구조 유지
* 커스텀 예외 설계를 통해 도메인 비즈니스 로직의 명확한 책임 분리

---

## 🧱 ErrorResponse DTO 구조

```java
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

    public static ErrorResponse of(int status, String code, String message, String path) {
        return ErrorResponse.builder()
            .status(status)
            .code(code)
            .message(message)
            .path(path)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

| 필드명         | 설명                         |
| ----------- | -------------------------- |
| `status`    | HTTP 응답 상태 코드              |
| `code`      | 에러 코드 명칭 (enum name)       |
| `message`   | 사용자에게 전달될 에러 메시지           |
| `path`      | 요청 경로 (URI)                |
| `timestamp` | 에러 발생 시간 (`LocalDateTime`) |

> ✅ `timestamp`를 포함하는 이유:
>
> * 에러 발생 시각을 기록하여 서버 로그와의 정합성 및 추적 용이
> * 분산 시스템에서 시계열 에러 모니터링 가능
> * 사용자 입장에서 에러 발생 시각 확인 가능

---

## ⚙️ 커스텀 예외 클래스 설계 원칙

### 🧱 BusinessException

```java
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

### 🧱 ValidationException

```java
public class ValidationException extends BusinessException {
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
```

### 🧱 NotFoundException

```java
public class NotFoundException extends BusinessException {
    public NotFoundException(String resourceName, Object identifier) {
        super(ErrorCode.NOT_FOUND, String.format("%s(%s)를 찾을 수 없습니다.", resourceName, identifier));
    }
}
```

---

## 🧾 ErrorCode Enum 설계

```java
@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1002, "아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2001, "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(2002, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
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
```

| 구간    | 코드 범위      | 설명                 |
| ----- | ---------- | ------------------ |
| 사용자   | 1000\~1999 | 사용자 관련 에러          |
| 인증/인가 | 2000\~2999 | 인증, 토큰 등           |
| 공통 오류 | 9000\~9999 | 서버 오류, 유효성 등 공통 오류 |

---

## 🛡️ GlobalExceptionHandler 요약

* `@ControllerAdvice`와 `@ExceptionHandler` 조합으로 글로벌 예외 처리 담당
* 발생한 예외 유형에 따라 `ErrorResponse`를 구성하여 `ResponseEntity`로 반환

| 예외 클래스                            | 처리 메서드 예시                     | 설명                               |
| --------------------------------- | ----------------------------- | -------------------------------- |
| `BusinessException`               | `handleBusiness()`            | 도메인 비즈니스 예외 처리                   |
| `ValidationException`             | `handleValidException()`      | 서비스 계층 유효성 검증 실패 처리              |
| `MethodArgumentNotValidException` | `handleValidException()`      | @Valid 유효성 검사 실패                 |
| `ConstraintViolationException`    | `handleConstraintViolation()` | RequestParam, PathVariable 검증 실패 |
| `NoHandlerFoundException`         | `handleNotFound()`            | 존재하지 않는 URI 요청                   |
| `Exception`                       | `handleUnknown()`             | 처리되지 않은 모든 예외의 fallback          |

---

## ✅ 테스트 전략

| 테스트 항목         | 기대 결과                         |
| -------------- | ----------------------------- |
| 유효성 검사 실패      | 400 + VALIDATION\_FAILED      |
| 존재하지 않는 리소스 요청 | 404 + NOT\_FOUND              |
| 정의된 비즈니스 예외    | 정의된 상태 코드 + 커스텀 메시지           |
| 처리되지 않은 예외     | 500 + INTERNAL\_SERVER\_ERROR |

---

📁 문서 위치 권장: `docs/EXCEPTION.md`
🔄 이 문서는 커밋 이력을 통해 유지 관리되며, 정책 변경 시 이슈 기반으로 논의 후 수정합니다.
