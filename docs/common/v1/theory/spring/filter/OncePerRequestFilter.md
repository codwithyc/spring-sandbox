# OncePerRequestFilter

## 1. 개요

`OncePerRequestFilter`는 Spring Web에서 제공하는 filter 기반 추상 클래스입니다.
이름 그대로 하나의 요청에 대해 filter 로직이 한 번 실행되도록 돕습니다.

Servlet filter는 HTTP 요청이 컨트롤러에 도달하기 전과 응답이 반환되기 전에 공통 처리를 할 수 있는 지점입니다.
인증, 로깅, request id 설정, CORS 처리 같은 횡단 관심사를 filter에서 처리할 수 있습니다.

`OncePerRequestFilter`는 이런 filter를 만들 때 직접 `Filter`를 구현하는 것보다
Spring 환경에서 더 안전하고 편하게 사용할 수 있는 기반 클래스로 볼 수 있습니다.

## 2. 왜 필요한가

request id는 컨트롤러에 들어오기 전에 준비되어야 합니다.
그래야 컨트롤러, 서비스, 예외 핸들러, 응답 생성기 모두 같은 request id를 사용할 수 있습니다.

만약 컨트롤러에서 request id를 만들면 다음 문제가 생길 수 있습니다.

- 컨트롤러에 도달하기 전에 발생한 예외에는 request id를 붙이기 어렵습니다.
- 모든 컨트롤러에서 같은 코드를 반복해야 합니다.
- 로그 MDC 설정 시점이 늦어질 수 있습니다.

filter는 컨트롤러보다 앞단에서 실행되므로 request id 같은 공통 요청 메타데이터를 준비하기에 적합합니다.

## 3. 동작 흐름

`OncePerRequestFilter`를 상속하면 주로 `doFilterInternal` 메서드를 구현합니다.

기본 흐름은 다음과 같습니다.

1. HTTP 요청이 들어옵니다.
2. Servlet filter chain이 실행됩니다.
3. `OncePerRequestFilter`가 현재 요청에 대해 이미 실행되었는지 확인합니다.
4. 아직 실행되지 않았다면 `doFilterInternal`을 호출합니다.
5. 구현 로직에서 공통 처리를 수행합니다.
6. `chain.doFilter(request, response)`로 다음 filter 또는 컨트롤러에 요청을 넘깁니다.
7. 응답이 돌아오면 후처리 또는 정리 작업을 수행할 수 있습니다.

## 4. 프로젝트 사용 방식

본 프로젝트에서는 `RequestIdFilter`가 `OncePerRequestFilter`를 상속합니다.

역할은 다음과 같습니다.

- `X-Request-Id` 헤더 읽기
- 헤더가 없을 때 UUID 생성
- request attribute에 request id 저장
- 응답 헤더 `X-Request-Id` 설정
- MDC에 `requestId` 저장
- 요청 종료 후 MDC 정리

핵심 흐름은 다음과 같습니다.

```java
String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
        .filter(v -> !v.isBlank())
        .orElse(UUID.randomUUID().toString());

request.setAttribute(RequestIds.REQUEST_ID_ATTR, requestId);
response.setHeader(REQUEST_ID_HEADER, requestId);
MDC.put("requestId", requestId);

try {
    chain.doFilter(request, response);
} finally {
    MDC.remove("requestId");
}
```

## 5. request attribute를 사용하는 이유

filter에서 결정한 request id는 이후 계층에서도 필요합니다.
이를 위해 `HttpServletRequest`의 attribute에 값을 저장합니다.

request attribute는 하나의 요청 처리 동안 서버 내부에서 공유할 수 있는 저장 공간입니다.
헤더와 달리 외부 클라이언트에게 직접 노출되는 계약이 아니라,
서버 내부 컴포넌트가 같은 요청 문맥에서 값을 공유하기 위해 사용합니다.

본 프로젝트에서는 `RequestIds.REQUEST_ID_ATTR` 키로 request id를 저장하고,
`ApiEnvelopes`와 `GlobalExceptionHandler`가 이 값을 읽습니다.

## 6. 주의사항

filter에서는 반드시 다음을 주의해야 합니다.

- `chain.doFilter`를 호출하지 않으면 요청이 다음 단계로 진행되지 않습니다.
- MDC 같은 thread-local 성격의 값은 `finally`에서 정리해야 합니다.
- filter 순서가 중요한 경우 `@Order`나 filter registration 정책을 별도로 검토해야 합니다.
- 요청 body를 직접 읽는 filter는 downstream에서 body를 다시 읽지 못하게 만들 수 있으므로 주의해야 합니다.

현재 `RequestIdFilter`는 요청 body를 읽지 않고 헤더와 attribute만 다루므로 비교적 안전한 공통 filter입니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `src/main/java/com/mycom/springsandbox/common/web/RequestIds.java`
- `docs/common/v1/adr/ADR-002-id-policy.md`
- `docs/common/v1/theory/http/XRequestId.md`
- `docs/common/v1/theory/slf4j/MDC.md`
