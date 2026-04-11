# ADR-004: Business Exception Policy

- Status: Accepted
- Deciders: spring-sandbox maintainers
- Date: 2026-04-11

## 1. Context

본 프로젝트는 공통 에러 응답과 일관된 예외 처리를 제공하기 위해
`BusinessException` 기반의 비즈니스 예외 계층을 사용합니다.

기존 `EXCEPTION_HANDLING` 문서는 예외 클래스, 에러 코드, 글로벌 핸들러, 응답 포맷을 한 문서에서 함께 설명하고 있었습니다.
하지만 이 네 가지는 서로 연결되어 있으면서도 책임이 다릅니다.

- Exception은 코드 안에서 실패 상황의 의미를 표현합니다.
- ErrorCode는 클라이언트와 운영 도구가 해석하는 응답 계약을 표현합니다.
- Handler는 발생한 예외를 HTTP 응답으로 변환합니다.
- Response Envelope은 성공과 실패 응답의 외부 JSON 구조를 정의합니다.

이 문서는 그중 Exception 계층의 책임과 설계 기준을 결정합니다.
Handler와 Response Envelope의 세부 설계는 별도 ADR에서 다룹니다.

## 2. Scope

이 ADR은 다음 범위에 적용됩니다.

- `BusinessException` 기반 비즈니스 예외 계층
- 공통 대표 예외 클래스
- 도메인별 예외 클래스를 추가하는 기준
- Exception과 `ErrorCodeSpec`의 연결 방식

이 ADR은 다음 범위는 직접 다루지 않습니다.

- `GlobalExceptionHandler`의 상세 처리 방식
- 공통 응답 envelope 구조
- `ErrorCode` 번호 체계와 추가 절차
- Bean Validation, JSON 파싱 등 Spring framework 예외 매핑

## 3. Decision Drivers

다음 기준을 우선순위로 고려했습니다.

- 서비스 코드에서 실패 상황의 비즈니스 의미가 드러나야 합니다.
- 예외 클래스가 HTTP 응답 생성 책임까지 가지면 안 됩니다.
- 모든 에러 코드마다 예외 클래스를 1:1로 만들지 않아야 합니다.
- 공통 예외는 대표적인 실패 범주만 제공하고, 도메인 의미는 도메인 예외로 확장할 수 있어야 합니다.
- `GlobalExceptionHandler`가 개별 비즈니스 예외 클래스를 모두 알 필요가 없어야 합니다.

## 4. Decision

비즈니스 예외는 `BusinessException`을 공통 부모로 사용합니다.

구체적으로는 다음과 같이 결정합니다.

- 모든 비즈니스 예외는 `BusinessException`을 상속합니다.
- `BusinessException`은 `ErrorCodeSpec`을 보관합니다.
- `BusinessException`은 checked exception이 아니라 `RuntimeException`으로 유지합니다.
- 공통 예외 클래스는 대표적인 실패 범주만 제공합니다.
- 도메인별 예외는 필요할 때 도메인 패키지에서 공통 예외를 확장합니다.
- 모든 `ErrorCode`마다 예외 클래스를 만들지 않습니다.
- 예외 클래스는 HTTP 응답을 직접 만들지 않습니다.

현재 공통 대표 예외는 다음 범주를 가집니다.

- `InvalidInputException`
- `ValidationException`
- `UnauthorizedException`
- `ForbiddenException`
- `NotFoundException`
- `ConflictException`
- `ServiceUnavailableException`

이 구조에서 예외는 "무슨 실패가 발생했는가"를 표현하고,
응답 생성은 `GlobalExceptionHandler`와 공통 응답 생성 컴포넌트가 담당합니다.

## 5. Rationale

### 5.1 `BusinessException`을 공통 부모로 둔 이유

비즈니스 예외를 하나의 부모 타입으로 묶으면,
서비스 계층에서는 도메인 실패를 명시적으로 던질 수 있고,
핸들러 계층에서는 `BusinessException` 하나를 기준으로 공통 응답을 만들 수 있습니다.

`BusinessException`이 `ErrorCodeSpec`을 가지는 이유는 다음과 같습니다.

- 예외와 응답 에러 코드의 연결이 명확해집니다.
- 공통 `ErrorCode`뿐 아니라 도메인별 `{Domain}ErrorCode`도 사용할 수 있습니다.
- 핸들러가 구체 enum 타입을 알지 않아도 HTTP 상태, 코드, 메시지 키를 얻을 수 있습니다.

### 5.2 공통 대표 예외를 둔 이유

`NotFoundException`, `ConflictException` 같은 대표 예외는 여러 도메인에서 반복되는 실패 범주입니다.
이 범주를 공통으로 두면 도메인 예외를 만들기 전에도 서비스 코드가 비교적 읽기 쉬워집니다.

예를 들어 클라이언트가 세부 도메인 코드를 구분할 필요가 없다면 다음처럼 사용할 수 있습니다.

```java
throw new NotFoundException("Member", memberId);
```

반대로 도메인별 코드가 필요하면 도메인 예외가 공통 예외를 확장할 수 있습니다.

```java
public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(Long memberId) {
        super(MemberErrorCode.MEMBER_NOT_FOUND, "회원(%s)를 찾을 수 없습니다.".formatted(memberId));
    }
}
```

이 방식은 공통 실패 범주와 도메인 의미를 동시에 유지합니다.

### 5.3 예외 클래스 1:1 생성을 피하는 이유

에러 코드는 응답 계약이고, 예외 클래스는 코드 안에서 실패 의미를 표현하는 도구입니다.
두 개념을 1:1로 고정하면 에러 코드가 하나 늘어날 때마다 클래스도 늘어납니다.

이는 작은 프로젝트에서는 단순해 보일 수 있지만,
도메인이 늘어나면 다음 문제가 생깁니다.

- 클래스 수가 빠르게 증가합니다.
- 메시지만 다른 경우에도 불필요한 타입이 생깁니다.
- 어떤 예외를 만들어야 하는지 판단 비용이 커집니다.
- 핸들러에서 개별 예외를 처리해야 한다는 오해가 생길 수 있습니다.

따라서 예외 클래스는 비즈니스 의미가 분명하고 여러 곳에서 반복되는 경우에만 추가합니다.

## 6. Alternatives Considered

### 6.1 `BusinessException`만 직접 사용

장점:

- 클래스 수가 가장 적습니다.
- 구현이 단순합니다.

단점:

- 서비스 코드에서 실패 의미가 드러나기 어렵습니다.
- 메시지 조립이 여러 곳에 흩어질 수 있습니다.
- 반복되는 비즈니스 실패 상황을 타입으로 표현하기 어렵습니다.

결론:

단순한 일회성 실패에는 사용할 수 있지만,
반복되는 대표 실패 범주까지 모두 `BusinessException` 직접 생성으로 처리하지는 않습니다.

### 6.2 모든 에러 코드마다 예외 클래스 생성

장점:

- 예외 타입만 보면 실패 원인을 알 수 있습니다.
- 도메인별 실패 상황을 세밀하게 표현할 수 있습니다.

단점:

- 에러 코드와 예외 클래스가 함께 폭증할 수 있습니다.
- 단순 메시지 차이에도 별도 클래스가 생깁니다.
- 유지보수 비용이 커집니다.

결론:

필요한 도메인 예외만 선별적으로 추가하는 방식이 더 적합합니다.

### 6.3 checked exception 사용

장점:

- 호출자가 예외 처리를 컴파일 시점에 인식할 수 있습니다.

단점:

- 서비스 계층 전반에 throws 선언이 퍼질 수 있습니다.
- Spring MVC의 전역 예외 처리 흐름과 맞물릴 때 코드가 장황해집니다.
- 대부분의 비즈니스 실패는 호출 위치에서 즉시 복구하기보다 공통 응답으로 변환하는 것이 목적입니다.

결론:

비즈니스 예외는 `RuntimeException` 기반으로 유지합니다.

## 7. Consequences

이 결정의 결과는 다음과 같습니다.

- 서비스 코드는 `BusinessException` 하위 예외를 던져 비즈니스 실패를 표현합니다.
- `GlobalExceptionHandler`는 `BusinessException` 하나를 기준으로 하위 예외를 공통 처리할 수 있습니다.
- 도메인별 예외는 필요할 때 공통 예외를 확장할 수 있습니다.
- 모든 에러 코드에 예외 클래스를 만들 필요는 없습니다.
- 예외 클래스는 응답 body나 `ResponseEntity`를 직접 만들지 않습니다.

추가로 주의할 점은 다음과 같습니다.

- 단순히 HTTP 상태를 감싸기 위한 예외 클래스 추가는 최소화해야 합니다.
- 프레임워크 예외와 비즈니스 예외를 혼동하지 않아야 합니다.
- 특정 예외만 별도 로깅, 알림, 응답 필드 구성이 필요하면 Handler ADR 기준에 따라 별도 핸들러를 검토합니다.

## 8. Implementation Notes

현재 구현 연결점은 다음과 같습니다.

- `BusinessException`은 `ErrorCodeSpec`을 보관합니다.
- `BusinessException`은 기본 메시지와 상세 메시지 생성자를 제공합니다.
- 공통 대표 예외는 `common.exception` 패키지에서 관리합니다.
- 도메인별 예외가 공통 예외를 확장할 수 있도록 `ErrorCodeSpec` 기반 생성자는 `protected`로 제공합니다.
- 서비스 계층에서 도메인 실패를 표현할 때 공통 예외 또는 도메인 예외를 사용합니다.

신규 예외를 추가할 때는 다음 기준을 따릅니다.

1. 기존 공통 예외로 표현 가능한지 확인합니다.
2. 실패 상황이 도메인 의미를 가지며 반복되는지 확인합니다.
3. 클라이언트가 세부 에러 코드를 구분해야 하는지 확인합니다.
4. 필요하면 도메인별 `ErrorCodeSpec` enum과 도메인 예외를 함께 추가합니다.
5. 특별한 응답 처리가 없다면 `GlobalExceptionHandler`에 개별 handler를 추가하지 않습니다.

## 9. Related Documents And Code

- `src/main/java/com/mycom/springsandbox/common/exception/BusinessException.java`
- `src/main/java/com/mycom/springsandbox/common/exception/NotFoundException.java`
- `src/main/java/com/mycom/springsandbox/common/exception/ConflictException.java`
- `src/main/java/com/mycom/springsandbox/common/exception/ValidationException.java`
- `src/main/java/com/mycom/springsandbox/common/error/ErrorCodeSpec.java`
- `src/main/java/com/mycom/springsandbox/common/enums/ErrorCode.java`
- `docs/common/v1/adr/ADR-003-error-code-management.md`
- `docs/common/v1/theory/java/exception/RuntimeException.md`
- `docs/common/v1/policy/EXCEPTION_HANDLING.md`
