# IllegalArgumentException

## 1. 개요

`IllegalArgumentException`은 Java의 unchecked exception 중 하나입니다.
메서드에 전달된 인자가 메서드가 요구하는 조건을 만족하지 않을 때 사용합니다.

핵심은 "호출자가 잘못된 값을 넘겼다"는 의미입니다.
타입은 맞지만 값의 의미가 잘못되었을 때 주로 사용합니다.

예:

```java
if (age < 0) {
    throw new IllegalArgumentException("age는 0 이상이어야 합니다.");
}
```

## 2. 언제 사용하는가

`IllegalArgumentException`은 다음 상황에서 적합합니다.

- 메서드 인자가 허용 범위를 벗어났습니다.
- 필수 인자가 비어 있거나 잘못된 형식입니다.
- enum이나 option 값이 지원 범위에 없습니다.
- 생성자 인자가 객체의 불변식을 깨뜨립니다.

중요한 점은 이 예외가 보통 프로그래밍 오류나 내부 API 사용 오류를 나타낸다는 것입니다.
외부 클라이언트 요청 검증 실패를 모두 이 예외로 표현하는 것은 적절하지 않을 수 있습니다.

## 3. BusinessException과의 차이

본 프로젝트의 `BusinessException`은 API 응답으로 변환될 비즈니스 실패를 표현합니다.
반면 `IllegalArgumentException`은 주로 코드 내부에서 잘못된 인자 사용을 표현합니다.

예를 들어 `ApiEnvelope` 생성자는 다음 불변식을 검증합니다.

- `success=true`이면 `error`는 `null`이어야 합니다.
- `success=false`이면 `data`는 `null`이어야 합니다.

이 불변식이 깨지면 이는 클라이언트의 비즈니스 요청 실패라기보다
서버 코드가 `ApiEnvelope`를 잘못 생성한 상황입니다.
따라서 `IllegalArgumentException`을 사용하는 것이 자연스럽습니다.

## 4. 프로젝트 사용 방식

본 프로젝트에서는 `ApiEnvelope` 생성자에서 잘못된 상태의 envelope 생성을 막기 위해 사용합니다.

```java
if (success && error != null) {
    throw new IllegalArgumentException("success=true 인 경우 error는 null이어야 합니다.");
}

if (!success && data != null) {
    throw new IllegalArgumentException("success=false 인 경우 data는 null이어야 합니다.");
}
```

이 코드는 외부 API 에러 응답을 만들기 위한 것이 아니라,
서버 내부 응답 객체의 불변식을 지키기 위한 방어 코드입니다.

## 5. 주의사항

주의할 점은 다음과 같습니다.

- 외부 요청 검증 실패를 무조건 `IllegalArgumentException`으로 표현하지 않습니다.
- API 클라이언트에게 내려갈 비즈니스 실패는 `BusinessException` 계층을 우선 검토합니다.
- `IllegalArgumentException` 메시지에 민감정보를 넣지 않습니다.
- 이 예외가 fallback handler로 내려가면 500 응답이 될 수 있으므로, 예상 가능한 비즈니스 실패와 구분해야 합니다.

## 6. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelope.java`
- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `docs/common/v1/adr/ADR-004-exception.md`
- `docs/common/v1/adr/ADR-006-response-envelope.md`
