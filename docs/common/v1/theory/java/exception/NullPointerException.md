# NullPointerException

## 1. 개요

`NullPointerException`은 Java에서 `null` 참조를 사용하려고 할 때 발생하는 unchecked exception입니다.

예를 들어 객체가 `null`인데 메서드를 호출하면 `NullPointerException`이 발생합니다.

```java
String name = null;
name.length(); // NullPointerException
```

이 예외는 Java에서 가장 흔히 만나는 런타임 예외 중 하나입니다.
대부분의 경우 예상 가능한 비즈니스 실패라기보다 코드가 null 가능성을 제대로 다루지 못했다는 신호입니다.

## 2. 왜 발생하는가

대표적인 발생 원인은 다음과 같습니다.

- `null` 객체의 메서드나 필드에 접근합니다.
- `null` 배열의 길이나 원소에 접근합니다.
- `null` 값을 unboxing하려고 합니다.
- 의존성 주입이 되지 않은 객체를 사용합니다.
- 메서드가 `null`을 반환할 수 있는데 호출자가 이를 고려하지 않습니다.

예:

```java
Integer value = null;
int number = value; // unboxing 중 NullPointerException
```

## 3. API 서버에서의 의미

API 서버에서 `NullPointerException`이 발생했다면 보통 다음 중 하나입니다.

- 서버 코드의 방어 로직이 부족합니다.
- 입력 검증이 적절한 계층에서 이루어지지 않았습니다.
- 객체 생성이나 의존성 주입 구성이 잘못되었습니다.
- null을 허용하지 않는 값에 대한 계약이 코드에 명확하지 않습니다.

따라서 `NullPointerException`을 예상 가능한 클라이언트 실패로 그대로 사용하면 안 됩니다.
클라이언트 입력이 누락된 경우라면 validation 또는 `BusinessException` 계층으로 명확히 표현하는 편이 좋습니다.

## 4. 프로젝트에서의 처리 방식

본 프로젝트의 `GlobalExceptionHandler`는 알 수 없는 예외를 fallback `Exception` handler에서 처리합니다.
`NullPointerException`도 별도로 처리하지 않으면 이 fallback 흐름으로 들어갈 수 있습니다.

이 경우 클라이언트에게는 내부 상세를 노출하지 않고 `INTERNAL_SERVER_ERROR`로 응답합니다.
서버 로그에는 stack trace를 남겨 원인을 추적합니다.

이 방식은 내부 오류 노출을 막는 데는 안전하지만,
예상 가능한 실패까지 `NullPointerException`으로 흘러가게 두면 API 품질이 떨어집니다.

## 5. 예방 방법

예방 방법은 다음과 같습니다.

- 요청 DTO는 Bean Validation으로 필수 값을 검증합니다.
- 생성자에서 필수 의존성을 주입받습니다.
- null이 가능한 값은 명시적으로 처리합니다.
- 불변 객체나 record를 활용해 상태를 단순하게 유지합니다.
- 비즈니스 실패는 `BusinessException` 계층으로 표현합니다.

예:

```java
if (member == null) {
    throw new NotFoundException("Member", memberId);
}
```

단, 실제 repository나 service에서는 `Optional` 또는 명시적인 조회 실패 처리 방식을 사용할 수 있습니다.

## 6. 주의사항

주의할 점은 다음과 같습니다.

- `NullPointerException` 메시지를 클라이언트에게 그대로 노출하지 않습니다.
- 예상 가능한 입력 누락은 validation 에러로 처리합니다.
- 예상 가능한 도메인 조회 실패는 `NotFoundException` 같은 비즈니스 예외로 처리합니다.
- fallback 500 응답이 반복된다면 null 처리 누락 또는 설계 문제로 보고 원인을 수정합니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `src/main/java/com/mycom/springsandbox/common/exception/NotFoundException.java`
- `docs/common/v1/adr/ADR-004-exception.md`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/theory/spring/validation/BeanValidation.md`
