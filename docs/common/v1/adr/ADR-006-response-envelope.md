# ADR-006: Common Response Envelope Policy

- Status: Accepted
- Deciders: spring-sandbox maintainers
- Date: 2026-04-11

## 1. Context

본 프로젝트는 API 응답에서 성공 데이터뿐 아니라 운영 메타데이터와 에러 정보를 일관되게 전달해야 합니다.

초기에는 성공 응답은 도메인 DTO를 그대로 반환하고,
에러 응답만 별도 구조로 반환하는 방식을 사용할 수 있습니다.
하지만 이 방식은 API가 늘어날수록 클라이언트와 서버 양쪽에 다음 부담을 만듭니다.

- 성공과 실패 응답의 최상위 구조가 달라집니다.
- request id, timestamp 같은 운영 메타데이터를 성공/실패 응답에 일관되게 담기 어렵습니다.
- 에러 응답만 별도 규칙을 가지면 클라이언트 파싱 로직이 분기됩니다.
- 컨트롤러마다 응답 구조를 직접 조립하면 응답 계약이 흔들릴 수 있습니다.

따라서 본 프로젝트는 성공 응답과 에러 응답을 하나의 공통 response envelope로 감싸되,
내부적으로는 성공 payload와 에러 payload를 명확히 분리하기로 합니다.

이 문서는 그 설계 의도와 응답 envelope 구조를 결정합니다.

## 2. Scope

이 ADR은 다음 범위에 적용됩니다.

- 공통 응답 envelope 구조
- 성공 응답의 `data` 표현
- 실패 응답의 `error` 표현
- 공통 메타데이터 `meta` 표현
- 성공 응답과 실패 응답의 내부 불변식
- `ApiEnvelope`, `ApiError`, `ApiMeta`, `ApiEnvelopes`의 역할

이 ADR은 다음 범위는 직접 다루지 않습니다.

- 개별 API의 도메인 DTO 설계
- 에러 코드 번호 체계
- 예외 클래스 계층 설계
- HTTP status code 세부 매핑
- pagination, cursor 등 목록 응답 전용 메타데이터 설계

## 3. Decision Drivers

다음 기준을 우선순위로 고려했습니다.

- 클라이언트가 성공과 실패 응답을 같은 최상위 구조로 파싱할 수 있어야 합니다.
- request id와 timestamp 같은 운영 메타데이터가 응답 전반에 일관되게 포함되어야 합니다.
- 성공 응답과 에러 응답은 내부적으로 혼합되지 않아야 합니다.
- 에러 정보는 코드, 메시지, 필드 에러, request id를 포함해 추적 가능해야 합니다.
- 컨트롤러가 응답 조립 세부사항을 반복하지 않아야 합니다.
- 향후 도메인 에러 코드, field error, 운영 메타데이터 확장에 대응할 수 있어야 합니다.

## 4. Decision

성공 응답과 에러 응답은 `ApiEnvelope<T>` 하나의 최상위 구조로 감쌉니다.

구체적으로는 다음과 같이 결정합니다.

- 최상위 응답은 `success`, `data`, `error`, `meta` 필드를 가집니다.
- 성공 응답은 `success=true`, `data` 존재, `error=null` 구조를 사용합니다.
- 실패 응답은 `success=false`, `data=null`, `error` 존재 구조를 사용합니다.
- `meta`는 성공과 실패 응답 모두에 포함합니다.
- `meta`에는 요청 추적용 `requestId`와 응답 생성 시각 `timestamp`를 포함합니다.
- 에러 상세 정보는 `ApiError`에 모읍니다.
- 응답 생성은 `ApiEnvelopes` 컴포넌트를 통해 수행합니다.
- `204 No Content`처럼 body가 없어야 하는 응답은 envelope를 만들지 않고 `ResponseEntity<Void>`로 반환할 수 있습니다.

즉, 외부 JSON의 최상위 계약은 하나로 통일하고,
내부 payload는 성공과 실패를 명확히 분리합니다.

## 5. Rationale

### 5.1 성공과 에러를 하나의 envelope로 묶은 이유

성공 응답과 에러 응답은 HTTP 관점에서는 서로 다른 상태를 가질 수 있지만,
클라이언트와 운영 도구 입장에서는 공통적으로 필요한 정보가 있습니다.

대표적으로 다음 정보가 모든 응답에서 필요합니다.

- 요청을 추적하기 위한 `requestId`
- 응답 생성 시각인 `timestamp`
- 응답이 성공인지 실패인지 나타내는 명시적 플래그

공통 envelope를 사용하면 클라이언트는 모든 응답에서 먼저 `success`와 `meta`를 확인할 수 있습니다.
또한 장애 상황에서도 request id와 timestamp를 같은 위치에서 읽을 수 있으므로,
사용자 문의, 서버 로그, API 응답을 연결하기 쉬워집니다.

이 구조는 API가 늘어나도 최상위 응답 계약을 반복해서 설명하지 않아도 된다는 장점이 있습니다.

### 5.2 성공과 에러 payload를 내부적으로 분리한 이유

최상위 구조를 하나로 묶더라도 성공 데이터와 에러 데이터를 같은 필드에 섞으면 응답 의미가 모호해집니다.

예를 들어 실패 응답에 `data`가 일부 들어가거나,
성공 응답에 `error`가 함께 들어가면 클라이언트는 어떤 값을 신뢰해야 하는지 판단해야 합니다.

따라서 `ApiEnvelope`는 다음 불변식을 가집니다.

- `success=true`이면 `error`는 반드시 `null`입니다.
- `success=false`이면 `data`는 반드시 `null`입니다.

이 불변식은 성공 payload와 실패 payload를 논리적으로 분리합니다.
결과적으로 최상위 계약은 통일하면서도 내부 의미는 명확하게 유지할 수 있습니다.

### 5.3 에러를 `ApiError`로 분리한 이유

에러 응답에는 단순 메시지 이상의 정보가 필요합니다.

현재 `ApiError`는 다음 정보를 담습니다.

- `code`: 클라이언트와 운영 도구가 해석하는 에러 코드
- `status`: HTTP 상태 코드 숫자
- `message`: 응답 메시지
- `messageKey`: 다국어 또는 클라이언트 메시지 매핑용 키
- `fieldErrors`: 필드 단위 검증 실패 정보
- `requestId`: 에러 객체 단위의 요청 추적 ID

에러를 별도 객체로 분리하면 성공 응답의 도메인 데이터와 에러 계약이 섞이지 않습니다.
또한 field error, message key, 도메인별 error code 같은 확장이 성공 DTO에 영향을 주지 않습니다.

`ApiError`가 `requestId`를 별도로 가지는 이유는 에러 객체만 로그나 클라이언트 처리에서 분리되어도
추적 정보를 잃지 않게 하기 위해서입니다.
동시에 `meta.requestId`도 유지하여 전체 응답 envelope 차원에서도 같은 요청을 추적할 수 있습니다.

### 5.4 `ApiEnvelopes`를 둔 이유

컨트롤러나 핸들러가 직접 `ApiEnvelope`를 만들면 응답 생성 시각, request id, HTTP status 설정이 흩어질 수 있습니다.

`ApiEnvelopes` 컴포넌트를 두면 다음 책임을 한 곳에 모을 수 있습니다.

- 성공 응답 생성
- 생성 응답의 `Location` 처리
- 실패 응답 생성
- 공통 `meta` 생성
- `Clock` 기반 timestamp 생성
- request id 조회

이 방식은 컨트롤러와 핸들러가 응답 구조 세부사항보다 유스케이스와 예외 매핑에 집중하게 합니다.

## 6. Alternatives Considered

### 6.1 성공 응답은 DTO 그대로 반환하고 에러만 envelope 사용

장점:

- 성공 응답 JSON이 더 짧습니다.
- 단순 CRUD API에서는 구현이 간단합니다.

단점:

- 성공과 실패 응답의 최상위 구조가 달라집니다.
- 클라이언트가 응답 파싱 경로를 다르게 가져가야 합니다.
- 성공 응답에서 request id와 timestamp 같은 운영 메타데이터를 일관되게 제공하기 어렵습니다.

결론:

운영 추적성과 공통 클라이언트 처리 흐름을 위해 채택하지 않습니다.

### 6.2 성공 응답 DTO와 에러 응답 DTO를 완전히 분리

장점:

- 성공과 실패 모델을 독립적으로 설계할 수 있습니다.
- 각 응답 타입의 의미가 분명합니다.

단점:

- 공통 메타데이터 위치가 달라질 수 있습니다.
- API 문서와 클라이언트 파싱 규칙이 복잡해질 수 있습니다.
- 컨트롤러와 핸들러가 서로 다른 최상위 응답 계약을 관리하게 됩니다.

결론:

최상위 구조는 `ApiEnvelope`로 통일하고, 내부 필드에서 성공과 실패를 분리합니다.

### 6.3 HTTP status code만으로 성공/실패 판단

장점:

- HTTP 표준 의미에 충실합니다.
- 응답 body가 단순해질 수 있습니다.

단점:

- 클라이언트가 body만 보고 응답 의미를 판단하기 어렵습니다.
- 애플리케이션 에러 코드, field error, request id 같은 프로젝트 메타데이터를 담을 표준 위치가 부족합니다.
- 일부 클라이언트 환경에서 HTTP status와 body 처리 흐름이 분리될 수 있습니다.

결론:

HTTP status code는 유지하되, body에도 `success`와 `error`를 명시합니다.

### 6.4 RFC 7807 Problem Details 사용

장점:

- 표준 에러 응답 형식을 활용할 수 있습니다.
- 외부 시스템과의 호환성이 좋을 수 있습니다.

단점:

- 성공 응답까지 포괄하는 공통 envelope 정책은 별도로 필요합니다.
- 현재 프로젝트의 `meta`, `messageKey`, `fieldErrors`, `requestId` 정책과 완전히 일치하지 않습니다.
- 도입 시 기존 `ApiError`와의 매핑 기준을 추가로 정해야 합니다.

결론:

현재 단계에서는 프로젝트 공통 envelope를 우선 사용합니다.
향후 외부 API 계약에서 Problem Details가 필요해지면 별도 ADR에서 검토합니다.

## 7. Consequences

이 결정의 결과는 다음과 같습니다.

- 성공과 실패 응답은 동일한 최상위 구조를 가집니다.
- 클라이언트는 모든 응답에서 `success`와 `meta`를 일관되게 확인할 수 있습니다.
- 성공 응답과 실패 응답의 payload는 `data`와 `error`로 분리됩니다.
- 에러 응답은 `ApiError`를 통해 코드, 메시지, 필드 에러, request id를 포함합니다.
- 컨트롤러와 핸들러는 `ApiEnvelopes`를 통해 응답 생성 책임을 위임합니다.

추가로 주의할 점은 다음과 같습니다.

- `success=true` 응답에 `error`를 넣으면 안 됩니다.
- `success=false` 응답에 `data`를 넣으면 안 됩니다.
- `204 No Content`는 body가 없어야 하므로 envelope를 강제하지 않습니다.
- 새로운 공통 메타데이터가 필요하면 `ApiMeta` 확장 영향을 검토해야 합니다.
- field error 구조를 바꾸면 validation 실패 응답 계약이 함께 바뀝니다.

## 8. Implementation Notes

현재 구현 연결점은 다음과 같습니다.

- `ApiEnvelope<T>`는 `success`, `data`, `error`, `meta`를 가집니다.
- `ApiEnvelope.ok(...)`는 성공 envelope를 생성합니다.
- `ApiEnvelope.fail(...)`은 실패 envelope를 생성합니다.
- `ApiEnvelope` 생성자는 성공/실패 payload 불변식을 검증합니다.
- `ApiMeta`는 `requestId`와 UTC 기준 `timestamp`를 가집니다.
- `ApiError`는 `ErrorCodeSpec` 기반으로 에러 응답 정보를 구성합니다.
- `ApiEnvelopes`는 `Clock`과 `HttpServletRequest`를 사용해 공통 `meta`를 생성합니다.
- `GlobalExceptionHandler`는 실패 응답 생성 시 `ApiEnvelopes.fail(...)`을 사용합니다.

신규 API 응답을 작성할 때는 다음 기준을 따릅니다.

1. body가 있는 성공 응답은 `ApiEnvelopes.ok(...)` 또는 `ApiEnvelopes.created(...)`를 사용합니다.
2. body가 없어야 하는 `204 No Content`는 `ApiEnvelopes.noContent()`를 사용할 수 있습니다.
3. 실패 응답은 컨트롤러에서 직접 만들기보다 예외를 던지고 `GlobalExceptionHandler` 흐름을 사용합니다.
4. 에러 응답을 직접 생성해야 하는 특수 상황에서도 `ApiError`와 `ApiEnvelopes.fail(...)` 흐름을 유지합니다.
5. response envelope 필드를 추가하거나 제거할 때는 클라이언트 계약 변경으로 보고 ADR을 갱신합니다.

## 9. Related Documents And Code

- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelope.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelopes.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiMeta.java`
- `src/main/java/com/mycom/springsandbox/common/error/ApiError.java`
- `src/main/java/com/mycom/springsandbox/common/error/FieldErrorItem.java`
- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/adr/ADR-001-utc-policy.md`
- `docs/common/v1/adr/ADR-002-id-policy.md`
- `docs/common/v1/adr/ADR-003-error-code-management.md`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/theory/http/XRequestId.md`
- `docs/common/v1/theory/spring/jackson/JsonInclude.md`
