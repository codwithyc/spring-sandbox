# Bean Validation

## 1. 개요

Bean Validation은 Java 객체의 값이 정해진 제약 조건을 만족하는지 검증하는 표준 방식입니다.

예를 들어 요청 DTO의 `name` 필드가 비어 있으면 안 된다면,
필드에 검증 애노테이션을 붙여 규칙을 선언할 수 있습니다.

```java
public record CreateMemberRequest(
        @NotBlank String name
) {
}
```

Spring MVC는 `@Valid` 또는 `@Validated`와 함께 Bean Validation을 사용해
컨트롤러 진입 시점에 요청 값을 자동으로 검증할 수 있습니다.

## 2. 왜 필요한가

API는 외부에서 들어오는 요청을 신뢰할 수 없습니다.
클라이언트가 잘못된 값을 보내거나, 필수 값을 빠뜨리거나, 타입에 맞지 않는 값을 보낼 수 있습니다.

Bean Validation을 사용하면 다음 장점이 있습니다.

- 요청 값 검증 규칙을 DTO 가까이에 선언할 수 있습니다.
- 컨트롤러와 서비스 코드에서 반복 검증을 줄일 수 있습니다.
- 검증 실패를 공통 예외 처리 흐름으로 보낼 수 있습니다.
- 필드 단위 에러 정보를 만들기 쉽습니다.

## 3. Spring MVC에서의 동작 흐름

요청 body DTO에 `@Valid`가 붙어 있으면 Spring MVC는 다음 흐름으로 검증합니다.

1. HTTP 요청 body를 읽습니다.
2. JSON을 DTO 객체로 변환합니다.
3. DTO에 선언된 Bean Validation 애노테이션을 검사합니다.
4. 검증에 실패하면 컨트롤러 메서드를 실행하지 않고 예외를 발생시킵니다.
5. `GlobalExceptionHandler`가 예외를 공통 에러 응답으로 변환합니다.

대표 예외는 다음과 같습니다.

- `MethodArgumentNotValidException`: `@RequestBody` DTO 검증 실패에서 자주 발생합니다.
- `ConstraintViolationException`: request parameter, path variable, method validation 등에서 발생할 수 있습니다.

## 4. fieldErrors의 의미

검증 실패는 단순히 "요청이 잘못되었다"로만 끝나지 않습니다.
클라이언트는 어느 필드가 왜 잘못되었는지 알아야 사용자에게 적절한 안내를 할 수 있습니다.

그래서 본 프로젝트는 `FieldErrorItem`으로 필드 단위 에러를 표현합니다.

```java
public record FieldErrorItem(
        String field,
        String code,
        String message
) {
}
```

각 필드는 다음 의미를 가집니다.

- `field`: 검증 실패가 발생한 필드 또는 property path
- `code`: 검증 실패 종류
- `message`: 검증 실패 메시지

이 정보는 `ApiError.fieldErrors`에 포함됩니다.

## 5. 프로젝트 사용 방식

본 프로젝트의 `GlobalExceptionHandler`는 validation 실패를 공통 응답으로 변환합니다.

`MethodArgumentNotValidException`은 `handleMethodArgumentNotValid`에서 처리합니다.
Spring의 `FieldError`를 `FieldErrorItem`으로 변환합니다.

```java
List<FieldErrorItem> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(this::toFieldErrorItem)
        .toList();
```

`ConstraintViolationException`은 `handleConstraintViolation`에서 처리합니다.
constraint violation의 property path와 message를 `FieldErrorItem`으로 변환합니다.

이때 에러 코드는 공통 `VALIDATION_ERROR`를 사용합니다.

## 6. JSON 파싱 실패와 validation 실패의 차이

JSON 파싱 실패와 validation 실패는 다릅니다.

JSON 파싱 실패는 요청 body 자체를 DTO로 읽을 수 없는 상황입니다.
예를 들어 JSON 문법이 깨졌거나 타입 변환이 불가능한 경우입니다.
이 경우 프로젝트는 `REQUEST_BODY_NOT_READABLE`로 응답합니다.

validation 실패는 DTO 객체로 읽는 데는 성공했지만,
객체 값이 검증 규칙을 만족하지 않는 상황입니다.
이 경우 프로젝트는 `VALIDATION_ERROR`와 field error 목록으로 응답합니다.

이 차이를 구분하면 클라이언트와 서버가 실패 원인을 더 명확하게 이해할 수 있습니다.

## 7. 주의사항

주의할 점은 다음과 같습니다.

- validation message에 민감정보를 넣으면 안 됩니다.
- 내부 클래스명이나 구현 상세를 그대로 노출하지 않는 것이 좋습니다.
- field name은 클라이언트가 이해할 수 있는 요청 필드명과 맞추는 것이 좋습니다.
- 복잡한 도메인 규칙은 DTO validation이 아니라 서비스 계층의 비즈니스 예외로 표현하는 편이 더 명확할 수 있습니다.
- validation 실패와 비즈니스 규칙 실패를 같은 것으로 취급하지 않아야 합니다.

## 8. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `src/main/java/com/mycom/springsandbox/common/error/FieldErrorItem.java`
- `src/main/java/com/mycom/springsandbox/common/enums/ErrorCode.java`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/adr/ADR-006-response-envelope.md`
