# UUID

## 1. 개요

UUID는 Universally Unique Identifier의 약자입니다.
이름 그대로 여러 시스템에서 충돌 가능성이 매우 낮은 식별자를 만들기 위한 형식입니다.

일반적으로 다음과 같은 문자열 형태로 표현됩니다.

```text
123e4567-e89b-12d3-a456-426614174000
```

UUID는 중앙 서버가 순번을 발급하지 않아도 각 시스템이 독립적으로 ID를 만들 수 있다는 장점이 있습니다.
그래서 request id, correlation id, 임시 파일명, 외부 공개용 식별자 등 다양한 곳에서 사용됩니다.

## 2. 왜 필요한가

분산 환경에서는 여러 클라이언트나 서버가 동시에 ID를 만들 수 있습니다.
이때 단순 증가 숫자를 사용하려면 중앙 발급기나 데이터베이스 sequence 같은 조정 지점이 필요합니다.

UUID는 이런 조정 없이도 각 프로세스가 독립적으로 ID를 만들 수 있게 합니다.

request id 관점에서 UUID가 유용한 이유는 다음과 같습니다.

- 서버와 클라이언트가 각자 생성하기 쉽습니다.
- Java, JavaScript, TypeScript 등 여러 환경에서 지원이 좋습니다.
- 별도 인프라 없이 충돌 가능성을 낮출 수 있습니다.
- 값 자체에 도메인 의미를 넣지 않아 추적용 ID로 사용하기 좋습니다.

## 3. Java에서의 UUID

Java는 표준 라이브러리에서 `java.util.UUID`를 제공합니다.

랜덤 UUID는 다음과 같이 만들 수 있습니다.

```java
String requestId = UUID.randomUUID().toString();
```

`UUID.randomUUID()`는 일반적으로 UUID version 4 형식의 랜덤 UUID를 생성합니다.
프로젝트에서 request id가 없을 때 서버가 새 값을 만들기 위해 이 API를 사용합니다.

## 4. 브라우저와 TypeScript에서의 UUID

브라우저 환경에서는 Web Crypto API의 `crypto.randomUUID()`를 사용할 수 있습니다.

예:

```typescript
const requestId = crypto.randomUUID();
```

이 방식은 프론트엔드가 `X-Request-Id`를 직접 생성해 백엔드로 전달할 때 사용할 수 있습니다.
프론트엔드와 백엔드가 UUID 형식을 공유하면 request id 계약이 단순해집니다.

## 5. UUID와 TSID 비교

UUID와 TSID는 모두 식별자 생성에 사용할 수 있지만 목적이 다릅니다.

UUID는 범용성과 충돌 회피에 강점이 있습니다.
반면 TSID는 시간 정보를 포함해 정렬 가능한 ID를 만들 때 유용합니다.

request id는 보통 "요청을 추적하기 위한 값"입니다.
요청 ID 자체를 시간순으로 정렬해야 하는 요구가 강하지 않다면 UUID가 더 단순하고 널리 이해됩니다.

반대로 데이터베이스 PK, 이벤트 ID, 시간순 정렬이 중요한 저장 ID라면 TSID 같은 대안을 검토할 수 있습니다.

## 6. 프로젝트 사용 방식

본 프로젝트에서는 `RequestIdFilter`가 클라이언트의 `X-Request-Id`를 우선 사용합니다.
헤더가 없거나 비어 있으면 서버가 UUID를 생성합니다.

```java
String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
        .filter(v -> !v.isBlank())
        .orElse(UUID.randomUUID().toString());
```

이 방식은 다음 의도를 가집니다.

- 프론트엔드가 만든 request id가 있으면 그 값을 유지합니다.
- 프론트엔드가 request id를 보내지 않아도 서버에서 추적 ID를 보장합니다.
- 서버에서 새 값을 만들 때는 Java 표준 API만 사용합니다.

## 7. 주의사항

UUID는 충돌 가능성이 매우 낮지만, 값 자체가 보안 토큰이 되는 것은 아닙니다.

주의할 점은 다음과 같습니다.

- UUID를 인증 토큰처럼 사용하면 안 됩니다.
- UUID에 사용자 정보나 도메인 의미를 담으려고 하면 안 됩니다.
- request id는 추적용 값이지 멱등성 보장 키가 아닙니다.
- 생성 시각 정렬이 필요한 경우 UUID만으로는 요구를 만족하지 못할 수 있습니다.

## 8. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `docs/common/v1/adr/ADR-002-id-policy.md`
- `docs/common/v1/theory/http/XRequestId.md`
