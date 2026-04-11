# ADR-003: Error Code Management Policy

- Status: Accepted
- Deciders: spring-sandbox maintainers
- Date: 2026-04-11

## 1. Context

본 프로젝트의 공통 에러 응답은 `ApiError`를 통해 `code`, `status`, `message`,
`messageKey`, `fieldErrors`, `requestId`를 제공합니다.

이 중 `code`는 클라이언트, 운영 로그, 모니터링 도구가 에러 원인을 안정적으로 구분하기 위한 계약 값입니다.
따라서 에러 코드는 단순 enum 값이 아니라 API 응답 계약의 일부로 관리되어야 합니다.

현재 공통 에러 코드는 `common.enums.ErrorCode`에서 관리하며,
`ErrorCode`는 `ErrorCodeSpec`을 구현합니다.
`ApiError`는 구체 enum 타입에 직접 의존하지 않고 `ErrorCodeSpec`을 통해 에러 코드 정보를 읽습니다.

이 구조는 공통 에러 코드뿐 아니라 향후 도메인별 에러 코드 enum도 같은 응답 포맷으로 처리할 수 있게 합니다.

따라서 이 문서는 "새로운 ErrorCode를 어떤 기준으로 추가하고, 공통 코드와 도메인 코드를 어떻게 관리할 것인가"를 결정합니다.

## 2. Scope

이 ADR은 다음 범위에 적용됩니다.

- 공통 에러 응답의 `code`, `status`, `messageKey`, `defaultMessage`
- `ErrorCodeSpec`을 구현하는 공통 및 도메인별 에러 코드 enum
- `BusinessException`과 에러 코드의 연결 방식
- 새 에러 코드를 추가할 때의 판단 기준과 절차

이 ADR은 다음 범위는 직접 다루지 않습니다.

- 예외 클래스 계층 전체 설계
- 다국어 메시지 리소스 파일 운영 방식
- 클라이언트 화면별 에러 메시지 표시 정책
- 로그 레벨, 알림, 모니터링 룰 설정

## 3. Decision Drivers

다음 기준을 우선순위로 고려했습니다.

- 클라이언트가 안정적으로 해석할 수 있는 에러 코드 계약을 유지해야 합니다.
- 공통 에러와 도메인 에러의 책임이 분리되어야 합니다.
- 단순 메시지 차이만으로 에러 코드가 과도하게 늘어나지 않아야 합니다.
- HTTP 상태 코드, 내부 에러 코드, 메시지 키가 일관된 규칙으로 관리되어야 합니다.
- `GlobalExceptionHandler`가 구체 에러 코드 enum 타입에 강하게 묶이지 않아야 합니다.

## 4. Decision

에러 코드는 `ErrorCodeSpec`을 구현하는 enum으로 관리합니다.

구체적으로는 다음과 같이 결정합니다.

- 공통 에러 코드는 `common.enums.ErrorCode`에서 관리합니다.
- 도메인별 세부 에러 코드가 필요하면 `{Domain}ErrorCode` enum을 만들고 `ErrorCodeSpec`을 구현합니다.
- 에러 코드 문자열은 `{PREFIX}-{NUMBER}` 형식을 사용합니다.
- 공통 에러 prefix는 `COM`을 사용합니다.
- 도메인 에러 prefix는 도메인별로 짧은 영문 대문자 prefix를 정의합니다. 예: `MEM`, `ORD`, `PAY`
- `NUMBER`는 4자리 숫자를 사용합니다.
- `4xxx`는 클라이언트 요청, 인증/인가, 비즈니스 규칙, 상태 충돌 계열로 사용합니다.
- `5xxx`는 서버 내부 오류, 외부 의존성, 일시적 서비스 불가 계열로 사용합니다.
- `messageKey`는 `error.{area}.{snake_case_name}` 형식을 사용합니다.
- 모든 에러 코드마다 예외 클래스를 1:1로 만들지 않습니다.

즉, 에러 코드는 응답 계약을 표현하고,
예외 클래스는 코드에서 실패 상황을 읽기 좋게 드러내기 위한 수단으로 분리합니다.

## 5. Rationale

### 5.1 `ErrorCodeSpec` 유지 이유

`ApiError`가 특정 enum인 `ErrorCode`에 직접 의존하면 도메인별 에러 코드를 추가할 때 공통 응답 구조가 함께 흔들릴 수 있습니다.

`ErrorCodeSpec`을 공통 인터페이스로 두면 다음 이점이 있습니다.

- 공통 에러 코드와 도메인 에러 코드를 같은 방식으로 응답에 담을 수 있습니다.
- `BusinessException`이 구체 enum 타입이 아니라 에러 코드 계약에만 의존할 수 있습니다.
- `GlobalExceptionHandler`는 `BusinessException` 하나를 기준으로 공통 처리할 수 있습니다.
- 도메인별 enum을 추가해도 공통 응답 생성 로직을 바꿀 필요가 줄어듭니다.

### 5.2 공통 코드와 도메인 코드 분리 이유

공통 에러 코드는 프레임워크, HTTP 요청 처리, 공통 API 계약에서 발생하는 에러를 표현합니다.

예시는 다음과 같습니다.

- 잘못된 입력값
- JSON 파싱 실패
- Bean Validation 실패
- 인증 필요
- 접근 거부
- 공통 Not Found
- 지원하지 않는 HTTP 메서드
- 지원하지 않는 미디어 타입
- 서버 내부 오류
- 외부 의존 서비스 사용 불가

도메인 에러 코드는 특정 비즈니스 도메인의 규칙 위반이나 상태 충돌을 표현합니다.

예시는 다음과 같습니다.

- 회원을 찾을 수 없음
- 이미 사용 중인 이메일
- 주문이 이미 취소됨
- 결제 금액이 주문 금액과 일치하지 않음

도메인 상황을 모두 공통 `ErrorCode`에 넣으면 공통 enum이 도메인 지식으로 비대해집니다.
반대로 공통 에러까지 도메인 enum에 흩어두면 공통 예외 처리 정책을 파악하기 어려워집니다.

따라서 공통 에러는 `ErrorCode`, 도메인 에러는 도메인별 `{Domain}ErrorCode`로 분리합니다.

### 5.3 새 에러 코드 추가 기준

새 에러 코드는 아래 조건 중 하나를 만족할 때 추가합니다.

- 클라이언트가 에러 원인을 코드로 구분해서 처리해야 합니다.
- 운영 로그나 모니터링에서 별도 집계가 필요한 에러입니다.
- 같은 에러 상황이 여러 곳에서 반복됩니다.
- 단순 메시지 차이가 아니라 비즈니스 의미가 다릅니다.
- 도메인 정책상 별도 계약으로 유지해야 하는 실패 사유입니다.

반대로 아래 경우에는 새 에러 코드를 추가하지 않습니다.

- 기존 코드에 detail message만 바꾸면 충분합니다.
- 로그 내부에서만 필요한 정보입니다.
- 일회성 검증 메시지입니다.
- 프레임워크 예외가 이미 공통 코드로 매핑되어 있습니다.
- 클라이언트가 별도로 분기하지 않는 단순 문구 차이입니다.

예를 들어 "회원을 찾을 수 없음"을 클라이언트가 별도 처리하지 않고 메시지만 다르게 보여주면
공통 `NOT_FOUND`와 detail message로 충분할 수 있습니다.
하지만 클라이언트가 `MEMBER_NOT_FOUND`를 기준으로 화면 흐름을 바꿔야 한다면
도메인 에러 코드 `MEM-4001`을 추가합니다.

## 6. Alternatives Considered

### 6.1 모든 에러 코드를 하나의 `ErrorCode` enum에서 관리

장점:

- 에러 코드를 한 파일에서 모두 볼 수 있습니다.
- 구현이 단순합니다.

단점:

- 도메인 에러가 늘어날수록 공통 enum이 비대해집니다.
- 공통 계층이 도메인 지식을 알게 됩니다.
- 도메인별 코드 소유권과 변경 이력을 분리하기 어렵습니다.

결론:

초기에는 단순하지만 도메인이 늘어날수록 관리 비용이 커질 수 있어 채택하지 않습니다.
공통 에러는 `ErrorCode`에 두고, 도메인 에러는 별도 enum으로 확장합니다.

### 6.2 HTTP 상태 코드만 사용

장점:

- 별도의 내부 에러 코드 관리가 필요 없습니다.
- 클라이언트가 HTTP 표준 상태만 해석하면 됩니다.

단점:

- 같은 `400 Bad Request` 안에서도 원인을 구분하기 어렵습니다.
- 운영 로그나 모니터링에서 세부 실패 원인을 집계하기 어렵습니다.
- 클라이언트가 화면 흐름을 세밀하게 분기하기 어렵습니다.

결론:

HTTP 상태 코드는 큰 실패 범주를 표현하고,
프로젝트 에러 코드는 구체 실패 원인을 표현하도록 함께 사용합니다.

### 6.3 모든 에러 코드마다 예외 클래스를 1:1로 생성

장점:

- 예외 클래스 이름만으로 실패 원인을 알 수 있습니다.
- 서비스 코드에서 도메인 의미가 명확하게 드러날 수 있습니다.

단점:

- 단순 에러 코드 추가에도 클래스가 계속 늘어납니다.
- 코드와 예외 클래스의 1:1 매핑을 유지하는 비용이 큽니다.
- 메시지만 다른 경우에도 불필요한 클래스가 생깁니다.

결론:

예외 클래스는 비즈니스 의미가 분명하고 여러 곳에서 반복되는 경우에만 추가합니다.
모든 에러 코드마다 예외 클래스를 만들지는 않습니다.

## 7. Consequences

이 결정의 결과는 다음과 같습니다.

- 공통 에러 코드는 `common.enums.ErrorCode`에 남습니다.
- 도메인별 세부 에러 코드는 `ErrorCodeSpec`을 구현하는 도메인 enum으로 확장할 수 있습니다.
- `GlobalExceptionHandler`는 `BusinessException`을 중심으로 공통 처리할 수 있습니다.
- 새 에러 코드 추가 시 클라이언트 분기 필요성, 운영 집계 필요성, 비즈니스 의미 차이를 먼저 검토해야 합니다.
- 에러 코드의 `code` 값은 외부 계약으로 보고 재사용하거나 의미를 바꾸지 않습니다.

추가로 주의할 점은 다음과 같습니다.

- `code`는 보안 토큰이나 내부 디버그 정보가 아닙니다.
- `message`는 변경될 수 있지만 `code`의 의미는 안정적으로 유지해야 합니다.
- 새 prefix가 필요하면 해당 도메인의 첫 에러 코드 추가 시 문서에 함께 기록해야 합니다.
- 특정 예외만 별도 로깅, 알림, 응답 필드 구성이 필요할 때만 별도 `@ExceptionHandler`를 검토합니다.

## 8. Implementation Notes

새 에러 코드를 추가할 때는 다음 순서로 진행합니다.

1. 기존 공통 또는 도메인 에러 코드로 표현 가능한지 먼저 확인합니다.
2. 새 코드가 필요하다면 공통 에러인지 도메인 에러인지 결정합니다.
3. 공통 에러이면 `common.enums.ErrorCode`에 추가합니다.
4. 도메인 에러이면 도메인별 enum이 있는지 확인하고, 없으면 `ErrorCodeSpec`을 구현하는 enum을 만듭니다.
5. `HttpStatus`, `code`, `messageKey`, `defaultMessage`를 함께 정의합니다.
6. 필요한 경우 `BusinessException` 기반 예외 클래스에서 해당 에러 코드를 사용합니다.
7. `GlobalExceptionHandler`가 이미 처리하는 예외 흐름인지 확인합니다.
8. 응답 계약이 바뀌면 테스트를 추가하거나 기존 테스트를 갱신합니다.
9. 새 prefix나 코드 범위 정책이 생기면 이 ADR 또는 별도 후속 ADR을 갱신합니다.

도메인 에러 코드 예시는 다음과 같습니다.

```java
public enum MemberErrorCode implements ErrorCodeSpec {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-4001", "error.member.not_found", "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEM-4002", "error.member.duplicate_email", "이미 사용 중인 이메일입니다.");

    // ErrorCodeSpec 구현
}
```

공통 코드를 그대로 사용하는 예시는 다음과 같습니다.

```java
throw new NotFoundException("Member", memberId);
```

도메인 코드가 필요한 경우 도메인 예외에서 공통 예외를 확장할 수 있습니다.

```java
public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(Long memberId) {
        super(MemberErrorCode.MEMBER_NOT_FOUND, "회원(%s)를 찾을 수 없습니다.".formatted(memberId));
    }
}
```

## 9. Related Documents And Code

- `src/main/java/com/mycom/springsandbox/common/enums/ErrorCode.java`
- `src/main/java/com/mycom/springsandbox/common/error/ErrorCodeSpec.java`
- `src/main/java/com/mycom/springsandbox/common/error/ApiError.java`
- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/policy/ADR_POLICY.md`
