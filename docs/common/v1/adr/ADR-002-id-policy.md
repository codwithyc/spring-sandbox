# ADR-002: UUID Request Id Policy For Request Tracing

- Status: Accepted
- Deciders: spring-sandbox maintainers
- Date: 2026-04-11

## 1. Context

본 프로젝트의 공통 응답 구조에는 요청 추적을 위한 `ApiMeta.requestId`가 포함됩니다.
또한 예외 응답의 `ApiError.requestId`와 로그 MDC의 `requestId`도 동일한 값을 사용하여
요청-응답-로그를 하나의 흐름으로 연결합니다.

현재 `RequestIdFilter`는 HTTP 요청의 `X-Request-Id` 헤더를 읽고,
값이 존재하면 해당 값을 요청 속성, 응답 헤더, 로그 MDC에 그대로 전파합니다.
헤더가 없거나 비어 있는 경우에는 서버가 새로운 request id를 생성합니다.

프론트엔드와 백엔드가 `X-Request-Id`를 요청 추적 헤더로 사용하기로 협의한 상황에서,
프론트엔드가 직접 request id를 생성해 전달할 수 있습니다. 이때 프론트엔드에서 생성하는 값의
기본 형식은 UUID로 가정합니다.

따라서 이 문서는 "`RequestIdFilter`가 직접 request id를 생성해야 할 때 어떤 ID 형식을 사용할 것인가"를 결정합니다.

## 2. Scope

이 ADR은 다음 범위에 적용됩니다.

- HTTP 요청 추적용 `X-Request-Id` 헤더
- 공통 응답 메타데이터의 `requestId`
- 공통 에러 응답의 `requestId`
- 로그 MDC에 저장되는 `requestId`
- `RequestIdFilter`가 헤더 부재 시 생성하는 request id 형식

이 ADR은 다음 범위는 직접 다루지 않습니다.

- 도메인 엔티티의 식별자 정책
- 데이터베이스 PK 또는 정렬 가능한 ID 정책
- 분산 트레이싱 표준(`traceparent` 등) 도입 여부
- 외부에서 전달된 `X-Request-Id` 값의 검증 또는 거부 정책

## 3. Decision Drivers

다음 기준을 우선순위로 고려했습니다.

- 프론트엔드와 백엔드가 동일한 request id 형식을 쉽게 공유할 수 있어야 합니다.
- 여러 언어, 프레임워크, 운영 도구에서 널리 이해되는 표준 형식이어야 합니다.
- 별도의 인프라나 중앙 발급기 없이 충분히 낮은 충돌 가능성을 가져야 합니다.
- 요청 추적 목적에 필요하지 않은 시간 정렬성이나 내부 생성 시각 노출을 피해야 합니다.
- 구현이 단순하고 JDK 표준 API만으로 생성할 수 있어야 합니다.

## 4. Decision

요청 추적용 request id의 서버 생성 형식은 UUID로 결정합니다.

구체적으로는 다음과 같이 결정합니다.

- 요청 추적 헤더는 `X-Request-Id`를 사용합니다.
- 클라이언트가 비어 있지 않은 `X-Request-Id`를 보내면 `RequestIdFilter`는 해당 값을 그대로 사용합니다.
- 클라이언트가 `X-Request-Id`를 보내지 않거나 빈 값으로 보내면 `RequestIdFilter`는 `UUID.randomUUID().toString()`으로 request id를 생성합니다.
- `RequestIdFilter`는 최종 request id를 요청 속성, 응답 헤더, 로그 MDC에 동일하게 기록합니다.
- 프론트엔드가 직접 `X-Request-Id`를 생성하는 경우에는 UUID 형식을 사용하는 것을 기본 계약으로 둡니다.

즉, request id는 도메인 식별자나 정렬 가능한 영속 ID가 아니라
요청-응답-로그를 연결하기 위한 상관관계 ID(correlation id)로 사용합니다.

## 5. Rationale

### 5.1 UUID 채택 이유

UUID는 request id처럼 시스템 경계를 넘어 전달되는 추적 ID에 적합합니다.

UUID를 선택한 이유는 다음과 같습니다.

- Java에서는 JDK 표준 API인 `UUID.randomUUID()`로 생성할 수 있습니다.
- 브라우저와 TypeScript 환경에서도 `crypto.randomUUID()` 등을 통해 생성하기 쉽습니다.
- 백엔드, 프론트엔드, 로그 분석 도구, 운영 도구에서 일반적으로 이해되는 형식입니다.
- 별도 라이브러리나 중앙 발급기 없이도 충돌 가능성이 충분히 낮습니다.
- 값 자체에 생성 시각이나 정렬 의미를 부여하지 않아도 되므로 추적용 ID의 책임이 단순하게 유지됩니다.

본 프로젝트의 request id는 "요청 하나를 식별해서 로그와 응답을 연결하는 값"입니다.
따라서 시간순 정렬, DB 인덱스 최적화, 사람이 읽기 쉬운 짧은 ID 같은 요구사항보다
시스템 간 호환성과 생성의 단순성이 더 중요합니다.

### 5.2 클라이언트 제공 값 전파 이유

프론트엔드가 `X-Request-Id`를 먼저 생성해 보내는 경우,
백엔드가 이를 새 값으로 덮어쓰면 프론트엔드 로그와 백엔드 로그를 같은 요청으로 묶기 어려워집니다.

따라서 `RequestIdFilter`는 클라이언트가 보낸 비어 있지 않은 `X-Request-Id`를 우선 사용하고,
응답 헤더에도 같은 값을 돌려줍니다. 이 방식은 클라이언트, 서버, 운영 로그가 하나의 request id를 공유하게 합니다.

단, 현재 구현은 외부에서 전달된 `X-Request-Id`가 UUID 형식인지 검증하지 않습니다.
검증 실패 시 요청을 거부할지, 서버에서 새 UUID로 대체할지는 별도의 API 계약 또는 보안 정책에서 결정할 수 있습니다.

## 6. Alternatives Considered

### 6.1 TSID

장점:

- 시간 기반 정렬이 가능합니다.
- DB 식별자나 이벤트 ID처럼 생성 순서가 의미 있는 값에 유용할 수 있습니다.
- UUID보다 짧거나 정렬 친화적인 표현을 사용할 수 있습니다.

단점:

- request id의 주요 목적은 시간순 정렬이 아니라 요청 상관관계 추적입니다.
- 프론트엔드와 백엔드 양쪽에서 동일한 생성 규칙과 라이브러리 선택을 추가로 맞춰야 합니다.
- UUID에 비해 운영 도구와 개발자에게 보편적으로 익숙한 형식은 아닙니다.
- 값에 생성 시각의 의미가 포함될 수 있어 request id에 불필요한 의미를 부여할 수 있습니다.

결론:

요청 추적용 request id에는 TSID의 시간 정렬성이 핵심 요구사항이 아니므로 채택하지 않았습니다.
TSID가 필요하다면 도메인 엔티티 ID, 이벤트 ID, 정렬 가능한 저장 식별자 정책에서 별도로 검토하는 것이 적합합니다.

### 6.2 서버에서만 생성하는 request id

장점:

- 서버가 모든 request id 형식을 통제할 수 있습니다.
- 외부에서 임의 값을 주입하는 문제를 줄일 수 있습니다.

단점:

- 프론트엔드가 요청 시작 시점부터 같은 ID로 로그를 남기기 어렵습니다.
- 클라이언트, 게이트웨이, 백엔드 로그를 하나의 ID로 연결하는 효과가 약해집니다.

결론:

프론트엔드와 백엔드가 `X-Request-Id`를 공유하기로 한 현재 상황에서는
클라이언트 제공 값을 우선 전파하는 방식이 더 적합하다고 판단했습니다.

### 6.3 임의 문자열 또는 접두어 기반 ID

장점:

- 사람이 읽기 쉬운 형식으로 만들 수 있습니다.
- `req-123`처럼 테스트나 예시에서 간단히 표현할 수 있습니다.

단점:

- 충돌 회피 규칙을 별도로 설계해야 합니다.
- 프론트엔드와 백엔드가 동일한 생성 규칙을 합의해야 합니다.
- 표준성이 낮아 운영 도구나 다른 시스템과 연동할 때 해석 비용이 증가할 수 있습니다.

결론:

요청 추적 ID의 표준성과 생성 안정성을 우선하여 채택하지 않았습니다.

## 7. Consequences

이 결정의 결과는 다음과 같습니다.

- `X-Request-Id`가 없는 요청도 서버에서 UUID 기반 request id를 부여받습니다.
- 프론트엔드가 UUID 기반 `X-Request-Id`를 생성하면 백엔드는 같은 값을 응답과 로그에 전파합니다.
- 요청 추적 ID는 시간 정렬 가능한 ID가 아니라 상관관계 추적용 ID로 해석해야 합니다.
- `RequestIdFilter` 구현은 별도 외부 라이브러리 없이 JDK 표준 API만 사용합니다.

추가로 주의할 점은 다음과 같습니다.

- 현재 구현은 클라이언트가 전달한 `X-Request-Id` 형식을 검증하지 않습니다.
- request id를 보안 토큰, 인증 정보, 멱등성 키, 도메인 식별자로 사용하면 안 됩니다.
- 향후 `traceparent` 같은 분산 트레이싱 표준을 도입하더라도 `X-Request-Id`와의 관계는 별도로 정의해야 합니다.

## 8. Implementation Notes

현재 구현 연결점은 다음과 같습니다.

- `RequestIdFilter`는 `X-Request-Id` 헤더를 읽고, 비어 있지 않으면 해당 값을 사용합니다.
- `RequestIdFilter`는 헤더가 없거나 비어 있으면 `UUID.randomUUID().toString()`으로 request id를 생성합니다.
- `RequestIdFilter`는 request id를 `RequestIds.REQUEST_ID_ATTR` 요청 속성에 저장합니다.
- `RequestIdFilter`는 응답 헤더 `X-Request-Id`와 로그 MDC의 `requestId`에 동일한 값을 기록합니다.
- `ApiEnvelopes`는 요청 속성의 request id를 읽어 `ApiMeta.requestId`에 포함합니다.
- `GlobalExceptionHandler`는 동일한 request id를 `ApiError.requestId`에 포함합니다.

이 정책을 따르는 신규 코드에서는 request id를 새로 생성하기보다,
가능한 한 `RequestIds.REQUEST_ID_ATTR`에 저장된 값을 사용해야 합니다.

## 9. Related Documents And Code

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `src/main/java/com/mycom/springsandbox/common/web/RequestIds.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelopes.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiMeta.java`
- `src/main/java/com/mycom/springsandbox/common/error/ApiError.java`
- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/policy/ADR_POLICY.md`
