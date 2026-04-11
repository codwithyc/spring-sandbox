# RestControllerAdvice

## 1. 개요

`@RestControllerAdvice`는 Spring MVC에서 전역 예외 처리와 공통 컨트롤러 보조 로직을 구성할 때 사용하는 애노테이션입니다.

이 애노테이션은 `@ControllerAdvice`와 `@ResponseBody`의 성격을 함께 가집니다.
즉, 여러 컨트롤러에서 발생한 예외를 한 곳에서 처리하고,
반환값을 view 이름이 아니라 HTTP response body로 직렬화하는 데 적합합니다.

REST API 서버에서는 예외가 발생했을 때 HTML 에러 페이지가 아니라 JSON 에러 응답을 반환해야 하는 경우가 많습니다.
`@RestControllerAdvice`는 이런 전역 JSON 에러 응답 처리에 자주 사용됩니다.

## 2. 왜 필요한가

컨트롤러마다 `try-catch`로 예외를 처리하면 다음 문제가 생깁니다.

- 같은 예외 처리 코드가 반복됩니다.
- 컨트롤러별 응답 형식이 달라질 수 있습니다.
- request id, error code, field error 같은 공통 정보를 일관되게 담기 어렵습니다.
- 새로운 예외 정책이 생길 때 여러 컨트롤러를 수정해야 합니다.

`@RestControllerAdvice`를 사용하면 예외 처리 책임을 컨트롤러 밖으로 분리할 수 있습니다.
컨트롤러는 정상 비즈니스 흐름에 집중하고,
전역 handler는 예외를 공통 에러 응답으로 변환합니다.

## 3. 동작 방식

기본 흐름은 다음과 같습니다.

1. 컨트롤러 또는 그 하위 계층에서 예외가 발생합니다.
2. Spring MVC가 해당 예외를 처리할 수 있는 `@ExceptionHandler` 메서드를 찾습니다.
3. `@RestControllerAdvice`에 선언된 handler가 매칭되면 해당 메서드를 호출합니다.
4. handler가 `ResponseEntity` 또는 응답 DTO를 반환합니다.
5. 반환값은 JSON response body로 직렬화됩니다.

예:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiEnvelope<Void>> handleBusiness(BusinessException ex) {
        // 예외를 공통 에러 응답으로 변환
    }
}
```

## 4. 프로젝트 사용 방식

본 프로젝트에서는 `GlobalExceptionHandler`가 `@RestControllerAdvice`로 등록됩니다.

주요 역할은 다음과 같습니다.

- `BusinessException`을 공통 에러 응답으로 변환합니다.
- validation 예외를 field error 응답으로 변환합니다.
- Spring MVC framework 예외를 프로젝트 `ErrorCode`로 매핑합니다.
- 처리되지 않은 예외를 `INTERNAL_SERVER_ERROR`로 변환합니다.
- request id를 에러 응답에 포함합니다.

이 구조 덕분에 컨트롤러는 직접 에러 body를 만들 필요가 줄어듭니다.

## 5. `@ControllerAdvice`와의 차이

`@ControllerAdvice`는 전역 컨트롤러 보조 기능을 제공합니다.
하지만 반환값을 response body로 직렬화하려면 메서드나 클래스에 `@ResponseBody`가 필요할 수 있습니다.

`@RestControllerAdvice`는 REST API에 맞춰 `@ResponseBody` 성격을 포함합니다.
따라서 JSON API 서버의 전역 예외 처리에는 `@RestControllerAdvice`가 더 자연스럽습니다.

## 6. 주의사항

`@RestControllerAdvice`는 강력한 전역 처리 지점이므로 다음을 주의해야 합니다.

- 너무 넓은 `Exception` handler가 구체적인 예외 처리를 가리지 않도록 handler 우선순위를 이해해야 합니다.
- 내부 예외 메시지를 그대로 클라이언트에 노출하면 안 됩니다.
- 공통 응답 형식을 유지해야 합니다.
- handler 안에서 다시 예외가 발생하지 않도록 단순하고 안정적인 로직을 유지해야 합니다.
- 여러 advice 클래스가 있을 경우 적용 순서와 범위를 명확히 해야 합니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/theory/spring/exception/ResponseEntityExceptionHandler.md`
