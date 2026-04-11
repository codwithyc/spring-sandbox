# RuntimeException

## 1. 개요

`RuntimeException`은 Java의 unchecked exception 계층에 속하는 예외입니다.

Java 예외는 크게 checked exception과 unchecked exception으로 나눌 수 있습니다.
`RuntimeException`과 그 하위 타입은 unchecked exception이므로,
메서드 시그니처에 `throws`를 반드시 선언하지 않아도 됩니다.

본 프로젝트의 `BusinessException`은 `RuntimeException`을 상속합니다.

## 2. checked exception과 unchecked exception

checked exception은 컴파일러가 처리 여부를 검사하는 예외입니다.
메서드에서 checked exception을 던지면 호출자가 `try-catch`로 처리하거나 `throws`로 전파해야 합니다.

unchecked exception은 컴파일러가 처리 여부를 강제하지 않습니다.
`RuntimeException`과 그 하위 예외가 여기에 속합니다.

차이는 다음과 같습니다.

| 구분 | checked exception | unchecked exception |
| --- | --- | --- |
| 컴파일러 처리 강제 | 있음 | 없음 |
| 대표 부모 | `Exception` | `RuntimeException` |
| 사용 예 | 파일 I/O 실패 | 잘못된 인자, 상태 오류, 비즈니스 실패 |
| 코드 영향 | `throws` 전파가 많아질 수 있음 | 전역 예외 처리와 결합하기 쉬움 |

## 3. 비즈니스 예외에 RuntimeException을 쓰는 이유

웹 API에서 비즈니스 예외는 보통 호출 위치에서 즉시 복구하기보다,
전역 예외 처리 계층에서 HTTP 에러 응답으로 변환하는 경우가 많습니다.

예를 들어 회원을 찾지 못한 상황은 서비스 내부에서 복구하기보다
`404 Not Found` 응답으로 변환하는 것이 자연스러울 수 있습니다.

이런 예외를 checked exception으로 만들면 서비스와 컨트롤러 메서드에 `throws` 선언이 퍼질 수 있습니다.
반면 `RuntimeException` 기반으로 만들면 비즈니스 실패를 던지고,
`GlobalExceptionHandler`에서 공통 응답으로 변환할 수 있습니다.

## 4. 프로젝트 사용 방식

본 프로젝트에서는 `BusinessException`이 `RuntimeException`을 상속합니다.

```java
public class BusinessException extends RuntimeException {
    private final ErrorCodeSpec errorCode;
}
```

공통 대표 예외와 도메인 예외는 `BusinessException`을 상속합니다.

예:

```java
throw new NotFoundException("Member", memberId);
```

이 예외는 컨트롤러에서 직접 catch하지 않아도
`GlobalExceptionHandler`의 `@ExceptionHandler(BusinessException.class)`에서 처리됩니다.

## 5. 장점과 trade-off

장점은 다음과 같습니다.

- 서비스 코드가 `throws` 선언으로 복잡해지지 않습니다.
- 전역 예외 처리와 잘 맞습니다.
- 비즈니스 실패를 필요한 위치에서 간결하게 던질 수 있습니다.

단점은 다음과 같습니다.

- 컴파일러가 처리 여부를 강제하지 않습니다.
- 어떤 예외가 발생할 수 있는지 메서드 시그니처만 보고 알기 어렵습니다.
- 문서화와 테스트가 부족하면 실패 흐름을 놓칠 수 있습니다.

따라서 unchecked exception을 사용하더라도,
도메인 서비스 테스트와 ADR 문서로 실패 흐름을 명확히 남기는 것이 중요합니다.

## 6. 주의사항

`RuntimeException`을 사용한다고 해서 모든 예외를 무분별하게 던져도 되는 것은 아닙니다.

주의할 점은 다음과 같습니다.

- 예상 가능한 비즈니스 실패는 `BusinessException` 계층으로 표현합니다.
- 프로그래밍 오류와 비즈니스 실패를 구분합니다.
- 내부 예외 메시지를 그대로 클라이언트에 노출하지 않습니다.
- fallback `Exception` handler에 모든 실패를 의존하지 않습니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `src/main/java/com/mycom/springsandbox/common/exception/NotFoundException.java`
- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/adr/ADR-004-exception.md`
