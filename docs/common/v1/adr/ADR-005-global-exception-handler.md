# ADR-005: Global Exception Handler Policy

- Status: Accepted
- Deciders: spring-sandbox maintainers
- Date: 2026-04-11

## 1. Context

본 프로젝트는 모든 API 실패 응답을 공통 envelope 형식으로 반환합니다.
이를 위해 컨트롤러나 서비스 코드가 직접 에러 응답을 조립하지 않고,
전역 예외 처리 계층에서 예외를 공통 응답으로 변환합니다.

예외 처리 계층은 다음 두 종류의 실패를 다룹니다.

- 애플리케이션이 의도적으로 던지는 비즈니스 예외
- Spring MVC, Bean Validation, JSON 파싱 등 framework 계층에서 발생하는 예외

비즈니스 예외와 framework 예외의 발생 위치와 원인은 다르지만,
클라이언트에게는 동일한 공통 응답 계약으로 전달되어야 합니다.

따라서 이 문서는 `GlobalExceptionHandler`가 어떤 책임을 가지고,
어떤 기준으로 예외를 공통 에러 응답으로 변환할 것인지 결정합니다.

## 2. Scope

이 ADR은 다음 범위에 적용됩니다.

- `GlobalExceptionHandler`의 책임
- `BusinessException` 처리 방식
- Spring MVC와 validation 계열 예외 매핑 방식
- 알 수 없는 예외에 대한 fallback 처리 방식
- 예외 응답 생성 시 `ApiError`, `ApiEnvelope`, `requestId`를 연결하는 방식

이 ADR은 다음 범위는 직접 다루지 않습니다.

- 비즈니스 예외 클래스 계층 설계
- 에러 코드 번호 체계
- 성공 응답 envelope 설계
- 로그 수집 시스템이나 알림 시스템 설정

## 3. Decision Drivers

다음 기준을 우선순위로 고려했습니다.

- 모든 실패 응답은 가능한 한 동일한 응답 구조를 가져야 합니다.
- 컨트롤러와 서비스 계층은 응답 조립 책임에서 분리되어야 합니다.
- 비즈니스 예외는 개별 하위 타입마다 handler를 추가하지 않아도 처리되어야 합니다.
- framework 예외는 클라이언트가 이해 가능한 공통 에러 코드로 변환되어야 합니다.
- 알 수 없는 예외는 내부 상세 정보를 노출하지 않고 서버 오류로 처리되어야 합니다.
- 요청 추적을 위해 에러 응답에도 `requestId`가 포함되어야 합니다.

## 4. Decision

전역 예외 처리는 `@RestControllerAdvice` 기반 `GlobalExceptionHandler`에서 담당합니다.

구체적으로는 다음과 같이 결정합니다.

- `GlobalExceptionHandler`는 `ResponseEntityExceptionHandler`를 상속합니다.
- `BusinessException`은 하나의 `@ExceptionHandler(BusinessException.class)`에서 공통 처리합니다.
- `BusinessException` 하위 예외마다 개별 handler를 만들지 않습니다.
- Bean Validation의 `ConstraintViolationException`은 별도 handler에서 field error로 변환합니다.
- Spring MVC의 주요 framework 예외는 `ResponseEntityExceptionHandler` override 메서드에서 공통 에러 코드로 매핑합니다.
- 처리되지 않은 `Exception`은 `INTERNAL_SERVER_ERROR`로 변환하고 서버 로그에 기록합니다.
- 에러 응답 body는 `ApiError`와 `ApiEnvelopes.fail(...)`을 통해 생성합니다.
- request id는 `RequestIds.REQUEST_ID_ATTR` 요청 속성에서 읽어 에러 응답에 포함합니다.

즉, Handler는 예외를 해석해 응답 계약으로 변환하는 경계 계층입니다.
예외 클래스는 실패 의미를 표현하고, Handler는 HTTP 응답을 만듭니다.

## 5. Rationale

### 5.1 `BusinessException` 하나로 처리하는 이유

`BusinessException`은 `ErrorCodeSpec`을 가지고 있으므로,
Handler는 구체 예외 타입을 몰라도 HTTP 상태, 내부 에러 코드, 메시지 키, 기본 메시지를 얻을 수 있습니다.

따라서 `NotFoundException`, `ConflictException`, 도메인별 `MemberNotFoundException` 같은 하위 예외가 추가되어도
특별한 응답 차이가 없다면 별도 handler가 필요하지 않습니다.

이 방식의 장점은 다음과 같습니다.

- 예외 클래스 추가가 Handler 수정으로 이어지지 않습니다.
- 비즈니스 예외 처리 흐름이 한 곳에 모입니다.
- 도메인 예외와 공통 예외를 동일한 응답 계약으로 변환할 수 있습니다.

### 5.2 framework 예외를 override로 처리하는 이유

Spring MVC는 요청 본문 파싱 실패, HTTP 메서드 불일치, 지원하지 않는 미디어 타입, validation 실패 등
컨트롤러 진입 전후에 다양한 예외를 발생시킵니다.

이 예외들을 Spring 기본 응답으로 그대로 두면 프로젝트의 공통 envelope 계약이 깨질 수 있습니다.
따라서 `ResponseEntityExceptionHandler`의 override 지점에서 framework 예외를 공통 `ErrorCode`와 `ApiError`로 변환합니다.

현재 대표 매핑은 다음과 같습니다.

- `MethodArgumentNotValidException` -> `VALIDATION_ERROR`
- `HttpMessageNotReadableException` -> `REQUEST_BODY_NOT_READABLE`
- `HttpRequestMethodNotSupportedException` -> `METHOD_NOT_ALLOWED`
- `HttpMediaTypeNotSupportedException` -> `UNSUPPORTED_MEDIA_TYPE`
- `HttpMediaTypeNotAcceptableException` -> `NOT_ACCEPTABLE`
- `MissingServletRequestParameterException` -> `INVALID_INPUT`
- `TypeMismatchException` -> `INVALID_INPUT`
- `NoHandlerFoundException` -> `NOT_FOUND`

### 5.3 알 수 없는 예외를 fallback 처리하는 이유

알 수 없는 예외는 클라이언트에게 내부 구현 정보를 노출하면 안 됩니다.
하지만 서버 운영자는 원인을 추적할 수 있어야 합니다.

따라서 `Exception` fallback handler는 다음 원칙을 따릅니다.

- 서버 로그에는 예외 stack trace를 기록합니다.
- 클라이언트 응답에는 `INTERNAL_SERVER_ERROR`의 기본 메시지를 사용합니다.
- request id를 응답에 포함해 클라이언트 문의와 서버 로그를 연결할 수 있게 합니다.

## 6. Alternatives Considered

### 6.1 컨트롤러별 예외 처리

장점:

- 컨트롤러 상황에 맞는 응답을 세밀하게 만들 수 있습니다.

단점:

- 응답 구조가 컨트롤러마다 달라질 수 있습니다.
- 같은 예외 처리 코드가 반복됩니다.
- 공통 request id, field error, 에러 코드 정책을 일관되게 적용하기 어렵습니다.

결론:

공통 API 계약을 유지하기 위해 전역 예외 처리 방식을 사용합니다.

### 6.2 비즈니스 예외 하위 타입별 handler 작성

장점:

- 예외 타입별로 세밀한 처리 흐름을 만들 수 있습니다.

단점:

- 새 예외 클래스가 생길 때마다 Handler 변경이 필요할 수 있습니다.
- 대부분의 비즈니스 예외는 `ErrorCodeSpec`만 다르고 응답 생성 방식은 동일합니다.
- Handler가 도메인 예외를 과도하게 알게 됩니다.

결론:

특별한 응답 차이가 없는 비즈니스 예외는 `BusinessException` handler 하나로 처리합니다.
개별 handler는 별도 로깅, 알림, 응답 필드 구성이 필요한 경우에만 추가합니다.

### 6.3 Spring 기본 에러 응답 사용

장점:

- 별도 handler 구현이 줄어듭니다.

단점:

- 프로젝트의 공통 envelope 계약과 맞지 않습니다.
- `requestId`, `messageKey`, `fieldErrors` 같은 프로젝트 메타데이터를 일관되게 담기 어렵습니다.
- 클라이언트가 성공 응답과 에러 응답을 서로 다른 방식으로 파싱해야 합니다.

결론:

공통 응답 계약과 운영 추적성을 위해 Spring 기본 에러 응답을 그대로 사용하지 않습니다.

## 7. Consequences

이 결정의 결과는 다음과 같습니다.

- 컨트롤러와 서비스 계층은 에러 응답 조립 책임에서 분리됩니다.
- 비즈니스 예외 하위 클래스가 추가되어도 대부분 Handler 수정이 필요 없습니다.
- framework 예외도 프로젝트의 공통 에러 응답 구조로 반환됩니다.
- 알 수 없는 예외는 내부 정보를 숨기고 서버 로그와 request id로 추적합니다.
- 에러 응답 형식은 `ApiError`와 `ApiEnvelope` 정책을 따릅니다.

추가로 주의할 점은 다음과 같습니다.

- fallback `Exception` handler가 너무 넓기 때문에, 예상 가능한 실패는 비즈니스 예외나 framework override로 명확히 분류해야 합니다.
- framework 예외 매핑을 추가할 때는 적절한 `ErrorCode`가 있는지 먼저 확인해야 합니다.
- 별도 handler를 추가할 때는 공통 envelope 형식이 유지되는지 확인해야 합니다.

## 8. Implementation Notes

현재 구현 연결점은 다음과 같습니다.

- `GlobalExceptionHandler`는 `@RestControllerAdvice`로 등록됩니다.
- `handleBusiness`는 `BusinessException`의 `ErrorCodeSpec`으로 `ApiError`를 생성합니다.
- `handleConstraintViolation`은 constraint violation을 `FieldErrorItem` 목록으로 변환합니다.
- Spring MVC 예외는 `ResponseEntityExceptionHandler` override 메서드에서 처리합니다.
- `frameworkFail`은 framework 예외를 `ApiEnvelope` 기반 `ResponseEntity<Object>`로 변환합니다.
- `handleUnknown`은 알 수 없는 예외를 로그로 남기고 `INTERNAL_SERVER_ERROR`로 응답합니다.
- `requestId`는 `RequestIds.REQUEST_ID_ATTR` 요청 속성에서 읽습니다.

신규 handler를 추가할 때는 다음 기준을 따릅니다.

1. 기존 `BusinessException` handler 또는 framework override로 처리 가능한지 확인합니다.
2. 별도 handler가 필요한 응답 차이, 로깅 차이, 알림 차이가 있는지 확인합니다.
3. 에러 코드는 기존 `ErrorCodeSpec`으로 표현 가능한지 확인합니다.
4. 응답은 반드시 `ApiError`와 `ApiEnvelopes.fail(...)` 흐름을 사용합니다.
5. request id가 누락되지 않는지 테스트합니다.

## 9. Related Documents And Code

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `src/main/java/com/mycom/springsandbox/common/error/ApiError.java`
- `src/main/java/com/mycom/springsandbox/common/error/FieldErrorItem.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelopes.java`
- `src/main/java/com/mycom/springsandbox/common/web/RequestIds.java`
- `docs/common/v1/adr/ADR-003-error-code-management.md`
- `docs/common/v1/adr/ADR-004-exception.md`
- `docs/common/v1/theory/spring/annotation/RestControllerAdvice.md`
- `docs/common/v1/theory/spring/exception/ResponseEntityExceptionHandler.md`
- `docs/common/v1/theory/spring/validation/BeanValidation.md`
- `docs/common/v1/policy/EXCEPTION_HANDLING.md`
