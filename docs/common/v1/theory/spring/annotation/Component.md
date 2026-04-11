# Component

## 1. 개요

`@Component`는 Spring container가 관리하는 Bean으로 클래스를 등록하기 위한 기본 stereotype 애노테이션입니다.

Spring 애플리케이션에서는 객체를 직접 `new`로 만들 수도 있지만,
공통 컴포넌트나 여러 곳에서 주입받아 사용하는 객체는 Spring container가 생성과 생명주기를 관리하게 하는 경우가 많습니다.
이때 클래스에 `@Component`를 붙이면 component scanning 과정에서 Bean으로 등록됩니다.

## 2. 왜 필요한가

Spring Bean으로 등록하면 다음 장점이 있습니다.

- 의존성 주입을 사용할 수 있습니다.
- 객체 생명주기를 Spring container가 관리합니다.
- 다른 Bean에서 생성자 주입으로 사용할 수 있습니다.
- 테스트에서 Bean 대체나 slice test 구성이 쉬워집니다.
- filter, service, helper component 같은 공통 로직을 애플리케이션 구성 안에 자연스럽게 포함할 수 있습니다.

## 3. 동작 방식

Spring Boot 애플리케이션은 기본적으로 main application class가 위치한 package 하위에서 component scanning을 수행합니다.

component scanning 중 `@Component`가 붙은 클래스를 발견하면,
Spring은 해당 클래스를 Bean definition으로 등록하고 필요한 시점에 객체를 생성합니다.

예:

```java
@Component
public class ApiEnvelopes {
    private final Clock clock;

    public ApiEnvelopes(Clock clock) {
        this.clock = clock;
    }
}
```

위 클래스는 `Clock` Bean을 생성자 주입으로 받고,
다른 컴포넌트에서 `ApiEnvelopes`를 주입받아 사용할 수 있습니다.

## 4. `@Service`, `@Repository`, `@Controller`와의 관계

`@Service`, `@Repository`, `@Controller`, `@RestController`도 넓게 보면 component stereotype입니다.
이 애노테이션들은 단순 Bean 등록을 넘어 클래스의 역할을 더 명확하게 드러냅니다.

- `@Component`: 일반적인 Spring 관리 컴포넌트
- `@Service`: 비즈니스 서비스 계층
- `@Repository`: persistence 계층
- `@Controller`: Spring MVC controller
- `@RestController`: REST API controller

역할이 분명한 계층에는 더 구체적인 stereotype을 사용하는 것이 좋습니다.
하지만 응답 생성 helper나 filter처럼 특정 계층 이름으로 분류하기 애매한 공통 컴포넌트에는 `@Component`가 적합합니다.

## 5. 프로젝트 사용 방식

본 프로젝트에서는 다음 클래스들이 `@Component`로 등록됩니다.

- `RequestIdFilter`
- `ApiEnvelopes`

`RequestIdFilter`는 요청마다 request id를 결정하고 MDC, 응답 헤더, request attribute에 반영합니다.
Spring Bean으로 등록되어야 web filter로 애플리케이션 흐름에 참여할 수 있습니다.

`ApiEnvelopes`는 공통 응답 envelope을 생성하는 컴포넌트입니다.
`Clock`을 생성자 주입받아 `ApiMeta.timestamp`를 생성합니다.

## 6. 주의사항

주의할 점은 다음과 같습니다.

- 모든 클래스를 무조건 `@Component`로 등록하면 Bean graph가 불필요하게 커질 수 있습니다.
- 상태를 가진 mutable Bean은 thread safety를 신중하게 고려해야 합니다.
- 단순 DTO, record, enum, exception은 보통 Bean으로 등록하지 않습니다.
- 역할이 분명한 계층 클래스에는 `@Service`, `@Repository`, `@RestController` 같은 구체 stereotype을 우선 고려합니다.

## 7. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/web/RequestIdFilter.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelopes.java`
- `src/main/java/com/mycom/springsandbox/common/config/TimeConfig.java`
- `docs/common/v1/theory/spring/filter/OncePerRequestFilter.md`
