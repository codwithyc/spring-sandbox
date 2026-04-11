# X-Request-Id

## 1. 개요

`X-Request-Id`는 HTTP 요청 하나를 식별하기 위해 사용하는 요청 추적용 헤더입니다.

HTTP 자체가 모든 요청에 고유 ID를 자동으로 부여해 주지는 않습니다.
따라서 클라이언트, 서버, 로그 시스템이 같은 요청을 함께 추적하려면
요청마다 별도의 식별자를 정해 전달하는 방식이 필요합니다.

`X-Request-Id`는 이 목적을 위해 널리 사용되는 관례적 헤더 이름입니다.
표준 HTTP 필수 헤더는 아니지만, API 서버와 클라이언트가 합의하면 요청 추적을 위한 안정적인 계약으로 사용할 수 있습니다.

## 2. HTTP 관점에서의 의미

HTTP 요청은 기본적으로 독립적인 메시지입니다.
클라이언트가 서버에 요청을 보내고, 서버가 응답을 반환하면 하나의 교환이 끝납니다.

하지만 실무에서는 하나의 요청이 여러 시스템을 거칠 수 있습니다.

- 브라우저 또는 모바일 앱
- API gateway
- backend server
- 내부 서비스
- 데이터베이스
- 로그 수집 시스템

이때 요청이 실패하거나 지연되면 "사용자가 보낸 그 요청이 서버 로그의 어떤 라인과 연결되는가"를 찾아야 합니다.
`X-Request-Id`는 이 연결 고리 역할을 합니다.

예를 들어 클라이언트가 다음 헤더를 보냅니다.

```http
X-Request-Id: 123e4567-e89b-12d3-a456-426614174000
```

서버는 같은 값을 응답 헤더와 로그에 남길 수 있습니다.
그러면 클라이언트 에러 화면, 서버 응답, 서버 로그가 같은 ID로 연결됩니다.

## 3. 왜 필요한가

`X-Request-Id`가 없으면 장애 분석 시 다음 문제가 생길 수 있습니다.

- 같은 시간대에 들어온 여러 요청 중 어떤 요청이 실패했는지 찾기 어렵습니다.
- 클라이언트가 받은 에러 응답과 서버 로그를 연결하기 어렵습니다.
- 여러 서버 인스턴스가 있을 때 요청 흐름을 추적하기 어렵습니다.
- 예외 응답과 정상 응답의 운영 메타데이터를 일관되게 구성하기 어렵습니다.

`X-Request-Id`를 사용하면 다음 장점이 있습니다.

- 요청-응답-로그를 하나의 ID로 연결할 수 있습니다.
- 클라이언트가 문의할 때 request id를 함께 전달할 수 있습니다.
- 서버 로그에서 특정 요청만 빠르게 검색할 수 있습니다.
- 공통 응답 `meta.requestId`와 에러 응답 `error.requestId`에 같은 값을 담을 수 있습니다.

## 4. 생성 주체

request id는 클라이언트가 만들 수도 있고, 서버가 만들 수도 있습니다.

### 4.1 클라이언트 생성

클라이언트가 요청을 시작하는 시점에 request id를 만들면,
프론트엔드 로그와 백엔드 로그를 같은 ID로 묶을 수 있습니다.

예:

```text
Frontend log: requestId=123e4567-e89b-12d3-a456-426614174000
Backend log:  requestId=123e4567-e89b-12d3-a456-426614174000
```

### 4.2 서버 생성

클라이언트가 request id를 보내지 않으면 서버가 생성할 수 있습니다.
이 방식은 모든 요청에 추적 ID가 존재하도록 보장합니다.

서버 생성 방식만 사용하면 백엔드 내부 추적은 가능하지만,
프론트엔드에서 요청을 시작한 시점의 로그와 연결하기는 상대적으로 어렵습니다.

## 5. 프로젝트 사용 방식

본 프로젝트에서는 `RequestIdFilter`가 `X-Request-Id`를 처리합니다.

동작 흐름은 다음과 같습니다.

1. 요청 헤더에서 `X-Request-Id`를 읽습니다.
2. 값이 존재하고 비어 있지 않으면 그대로 사용합니다.
3. 값이 없거나 비어 있으면 서버에서 UUID를 생성합니다.
4. 최종 request id를 request attribute에 저장합니다.
5. 응답 헤더 `X-Request-Id`에 같은 값을 기록합니다.
6. 로그 MDC의 `requestId`에 같은 값을 저장합니다.
7. 요청 처리가 끝나면 MDC에서 `requestId`를 제거합니다.

이 값은 이후 `ApiMeta.requestId`, `ApiError.requestId`, 로그 MDC에서 사용됩니다.

## 6. 주의사항

`X-Request-Id`는 추적용 ID이지 보안 토큰이 아닙니다.

주의할 점은 다음과 같습니다.

- 인증이나 인가 판단에 사용하면 안 됩니다.
- 멱등성 키처럼 비즈니스 중복 처리 기준으로 사용하면 안 됩니다.
- 사용자 개인정보나 민감정보를 값에 넣으면 안 됩니다.
- 클라이언트가 보낸 값을 그대로 신뢰해 보안 판단을 하면 안 됩니다.
- 로그 검색 편의를 위해 너무 긴 값을 허용할지 여부는 별도 정책으로 정할 수 있습니다.

현재 프로젝트 구현은 클라이언트가 보낸 `X-Request-Id`의 형식이 UUID인지 검증하지 않습니다.
필요하면 별도 정책에서 허용 형식, 길이 제한, 거부 방식 또는 서버 재발급 방식을 결정할 수 있습니다.

## 7. 관련 개념

`X-Request-Id`와 비슷한 목적을 가진 개념으로 correlation id와 trace id가 있습니다.

- correlation id: 여러 로그나 이벤트를 하나의 흐름으로 묶는 식별자
- request id: HTTP 요청 하나를 추적하는 식별자
- trace id: 분산 tracing 시스템에서 전체 trace를 식별하는 값

작은 시스템에서는 request id와 correlation id를 비슷한 의미로 사용할 수 있습니다.
하지만 서비스가 많아지고 tracing 시스템을 도입하면 `traceparent` 같은 표준과의 관계를 별도로 정하는 것이 좋습니다.

## 8. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `src/main/java/com/mycom/springsandbox/common/web/RequestIds.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiMeta.java`
- `src/main/java/com/mycom/springsandbox/common/error/ApiError.java`
- `docs/common/v1/adr/ADR-002-id-policy.md`
- `docs/common/v1/theory/java/uuid/UUID.md`
- `docs/common/v1/theory/slf4j/MDC.md`
