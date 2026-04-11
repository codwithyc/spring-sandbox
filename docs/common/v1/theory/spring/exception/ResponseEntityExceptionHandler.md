# ResponseEntityExceptionHandler

## 1. 개요

`ResponseEntityExceptionHandler`는 Spring MVC가 제공하는 기본 예외 처리 기반 클래스입니다.
Spring MVC에서 자주 발생하는 framework 예외를 처리할 수 있는 여러 protected 메서드를 제공합니다.

예를 들어 다음과 같은 상황은 컨트롤러 비즈니스 로직에 도달하기 전이나,
Spring MVC 요청 처리 과정에서 발생할 수 있습니다.

- 요청 JSON을 읽을 수 없음
- `@Valid` 검증 실패
- 지원하지 않는 HTTP method
- 지원하지 않는 media type
- 필수 request parameter 누락
- path variable 또는 request parameter 타입 변환 실패

이런 예외를 프로젝트 공통 에러 응답으로 바꾸려면
`ResponseEntityExceptionHandler`의 메서드를 override하는 방식이 유용합니다.

## 2. 왜 필요한가

`@ExceptionHandler`만으로도 예외 처리는 가능합니다.
하지만 Spring MVC framework 예외는 이미 Spring이 기본 처리 흐름을 가지고 있습니다.
`ResponseEntityExceptionHandler`를 상속하면 그 흐름에 맞춰 필요한 지점만 override할 수 있습니다.

장점은 다음과 같습니다.

- Spring MVC의 표준 예외 처리 확장 지점을 사용할 수 있습니다.
- validation, message not readable, method not allowed 같은 예외를 명확한 메서드 단위로 처리할 수 있습니다.
- 프로젝트 공통 envelope 형식으로 변환하는 로직을 한 곳에 모을 수 있습니다.

## 3. 동작 방식

기본 흐름은 다음과 같습니다.

1. Spring MVC 요청 처리 중 framework 예외가 발생합니다.
2. `ResponseEntityExceptionHandler`가 해당 예외에 맞는 handler 메서드를 호출합니다.
3. 프로젝트에서 override한 메서드가 있으면 그 구현이 실행됩니다.
4. override 메서드는 예외를 프로젝트 `ErrorCode`와 `ApiError`로 변환합니다.
5. 최종적으로 `ResponseEntity`가 클라이언트에 반환됩니다.

예:

```java
@Override
protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
) {
    return frameworkFail(
            httpRequest(request),
            ErrorCode.REQUEST_BODY_NOT_READABLE,
            "요청 본문(JSON) 형식이 올바르지 않습니다.",
            List.of()
    );
}
```

## 4. 프로젝트 사용 방식

본 프로젝트의 `GlobalExceptionHandler`는 `ResponseEntityExceptionHandler`를 상속합니다.

현재 override하는 대표 메서드는 다음과 같습니다.

- `handleMethodArgumentNotValid`
- `handleHttpMessageNotReadable`
- `handleHttpRequestMethodNotSupported`
- `handleHttpMediaTypeNotSupported`
- `handleHttpMediaTypeNotAcceptable`
- `handleMissingServletRequestParameter`
- `handleTypeMismatch`
- `handleNoHandlerFoundException`

각 메서드는 Spring 예외를 프로젝트 공통 `ErrorCode`로 매핑하고,
`frameworkFail` 메서드를 통해 `ApiEnvelope` 기반 응답으로 변환합니다.

## 5. `@ExceptionHandler`와의 관계

`@ExceptionHandler`는 특정 예외 타입을 직접 처리하는 메서드에 사용합니다.
본 프로젝트에서는 `BusinessException`, `ConstraintViolationException`, fallback `Exception` 처리에 사용합니다.

반면 `ResponseEntityExceptionHandler` override는 Spring MVC가 이미 알고 있는 framework 예외의 기본 처리 지점을 바꾸는 방식입니다.

두 방식은 서로 경쟁 관계가 아니라 역할이 다릅니다.

- 비즈니스 예외: `@ExceptionHandler(BusinessException.class)`
- Spring MVC framework 예외: `ResponseEntityExceptionHandler` override
- 알 수 없는 예외: `@ExceptionHandler(Exception.class)`

## 6. 주의사항

주의할 점은 다음과 같습니다.

- override 메서드의 시그니처가 Spring 버전에 맞아야 합니다.
- framework 예외 응답도 프로젝트 envelope 형식을 유지해야 합니다.
- validation field error를 만들 때 내부 객체 전체를 노출하지 않아야 합니다.
- fallback `Exception` handler와 중복되지 않도록 예상 가능한 framework 예외는 명시적으로 매핑하는 것이 좋습니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `src/main/java/com/mycom/springsandbox/common/error/FieldErrorItem.java`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/theory/spring/annotation/RestControllerAdvice.md`
