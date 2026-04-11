# IllegalStateException

## 1. 개요

`IllegalStateException`은 Java의 unchecked exception 중 하나입니다.
객체나 시스템이 현재 작업을 수행하기에 올바른 상태가 아닐 때 사용합니다.

`IllegalArgumentException`이 "넘겨준 값이 잘못되었다"에 가깝다면,
`IllegalStateException`은 "지금 이 객체의 상태에서는 이 작업을 하면 안 된다"에 가깝습니다.

예:

```java
if (!started) {
    throw new IllegalStateException("시작되지 않은 작업은 종료할 수 없습니다.");
}
```

## 2. 언제 사용하는가

`IllegalStateException`은 다음 상황에서 적합합니다.

- 객체가 초기화되지 않았는데 사용하려고 합니다.
- 이미 닫힌 리소스를 다시 사용하려고 합니다.
- 메서드 호출 순서가 잘못되었습니다.
- 내부 상태가 불변식과 맞지 않습니다.

이 예외는 외부 입력값 자체보다 객체의 현재 상태가 문제일 때 사용합니다.

## 3. IllegalArgumentException과의 차이

두 예외는 모두 unchecked exception이지만 의미가 다릅니다.

| 예외 | 핵심 의미 | 예시 |
| --- | --- | --- |
| `IllegalArgumentException` | 전달된 인자가 잘못됨 | 음수 age 전달 |
| `IllegalStateException` | 현재 상태가 작업에 맞지 않음 | 이미 완료된 작업을 다시 완료 처리 |

구분 기준은 "문제가 인자에 있는가, 현재 상태에 있는가"입니다.

## 4. BusinessException과의 관계

도메인 상태 충돌은 경우에 따라 `BusinessException` 계층으로 표현하는 것이 더 적합할 수 있습니다.

예를 들어 "이미 취소된 주문은 다시 취소할 수 없다"는 외부 API 사용자에게 알려야 하는 비즈니스 규칙 실패입니다.
이런 경우에는 `ConflictException` 또는 도메인별 `OrderAlreadyCanceledException` 같은 비즈니스 예외가 더 자연스럽습니다.

반면 `IllegalStateException`은 서버 내부 객체 사용 순서나 구현 불변식 위반을 표현할 때 더 적합합니다.

## 5. 프로젝트 사용 기준

본 프로젝트에서 `IllegalStateException`은 다음 기준으로 사용합니다.

- 서버 내부 코드의 호출 순서나 상태 불변식이 깨진 경우
- 클라이언트에게 세부 비즈니스 에러 코드로 안내할 필요가 없는 내부 오류
- 복구 가능한 비즈니스 실패가 아니라 프로그래밍 오류에 가까운 상황

외부 요청에 대한 예상 가능한 도메인 실패는 `BusinessException` 계층을 우선 사용합니다.

## 6. 주의사항

주의할 점은 다음과 같습니다.

- 도메인 규칙 실패를 모두 `IllegalStateException`으로 처리하면 API 에러 코드 정책이 흐려질 수 있습니다.
- fallback handler에서 500으로 처리될 수 있으므로 예상 가능한 사용자 실패와 구분해야 합니다.
- 메시지에 내부 구현 상세나 민감정보를 넣지 않습니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `src/main/java/com/mycom/springsandbox/common/exception/ConflictException.java`
- `docs/common/v1/adr/ADR-004-exception.md`
- `docs/common/v1/theory/java/exception/IllegalArgumentException.md`
