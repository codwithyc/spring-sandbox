# MDC

## 1. 개요

MDC는 Mapped Diagnostic Context의 약자입니다.
로그를 남길 때 현재 실행 흐름에 붙일 수 있는 key-value 형태의 진단 컨텍스트입니다.

쉽게 말하면, 로그 메시지마다 직접 값을 넘기지 않아도
현재 요청과 관련된 값을 로그 패턴에서 자동으로 출력할 수 있게 도와주는 저장소입니다.

예를 들어 요청 처리 중 MDC에 다음 값을 넣어 둡니다.

```java
MDC.put("requestId", requestId);
```

로그 패턴이 `requestId`를 출력하도록 설정되어 있으면,
그 요청을 처리하는 동안 남는 로그에 같은 request id를 붙일 수 있습니다.

## 2. 왜 필요한가

서버 로그는 여러 요청이 동시에 섞여 기록됩니다.
특히 웹 서버는 여러 thread가 동시에 요청을 처리하므로,
시간순 로그만 보면 특정 요청의 흐름을 따라가기 어렵습니다.

MDC에 `requestId`를 넣으면 같은 요청에서 발생한 로그를 하나로 묶어 볼 수 있습니다.

예:

```text
[requestId=123e4567-e89b-12d3-a456-426614174000] controller start
[requestId=123e4567-e89b-12d3-a456-426614174000] service call
[requestId=123e4567-e89b-12d3-a456-426614174000] response fail
```

이렇게 하면 에러 응답의 `requestId`와 서버 로그를 쉽게 연결할 수 있습니다.

## 3. 동작 방식

MDC는 보통 현재 thread에 연결된 context로 동작합니다.
웹 요청이 thread 하나에서 처리되는 동안 MDC에 값을 넣으면,
그 thread에서 남는 로그가 해당 값을 참조할 수 있습니다.

기본 흐름은 다음과 같습니다.

1. 요청이 들어옵니다.
2. filter에서 request id를 결정합니다.
3. `MDC.put("requestId", requestId)`로 값을 저장합니다.
4. 컨트롤러, 서비스, 핸들러에서 로그를 남깁니다.
5. 로그 패턴이 MDC 값을 출력합니다.
6. 요청 처리가 끝나면 `MDC.remove("requestId")`로 값을 제거합니다.

## 4. 반드시 제거해야 하는 이유

웹 서버 thread는 요청마다 새로 만들어지는 것이 아니라 thread pool에서 재사용될 수 있습니다.

따라서 요청이 끝난 뒤 MDC 값을 제거하지 않으면,
다음 요청에서 이전 요청의 `requestId`가 잘못 출력될 수 있습니다.
이 문제는 로그 분석에서 매우 혼란스러운 결과를 만듭니다.

그래서 MDC 값은 보통 `try-finally`로 제거합니다.

```java
MDC.put("requestId", requestId);

try {
    chain.doFilter(request, response);
} finally {
    MDC.remove("requestId");
}
```

## 5. 프로젝트 사용 방식

본 프로젝트에서는 `RequestIdFilter`가 request id를 결정한 뒤 MDC에 저장합니다.

```java
MDC.put("requestId", requestId);
```

그리고 요청 처리가 끝나면 반드시 제거합니다.

```java
MDC.remove("requestId");
```

이 값은 서버 로그와 `ApiMeta.requestId`, `ApiError.requestId`를 연결하는 데 사용됩니다.

## 6. 주의사항

MDC를 사용할 때는 다음을 주의해야 합니다.

- 요청 종료 시 반드시 값을 제거해야 합니다.
- 비동기 처리나 다른 thread로 작업이 넘어가면 MDC가 자동으로 전달되지 않을 수 있습니다.
- 민감정보를 MDC에 넣으면 로그로 노출될 수 있습니다.
- 너무 많은 값을 MDC에 넣으면 로그가 복잡해지고 비용이 증가합니다.

현재 프로젝트에서는 request id처럼 추적에 필요한 최소 정보만 MDC에 넣는 방식을 권장합니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `docs/common/v1/adr/ADR-002-id-policy.md`
- `docs/common/v1/theory/http/XRequestId.md`
