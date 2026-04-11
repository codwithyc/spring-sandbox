# Override

## 1. 개요

Override는 Java에서 부모 클래스 또는 인터페이스가 제공한 메서드를
자식 클래스에서 다시 정의하는 객체지향 기능입니다.

쉽게 말하면, 부모가 이미 가진 동작의 이름과 형태는 유지하되
자식 클래스가 자신에게 맞는 동작으로 바꾸는 것입니다.

Java에서는 override한 메서드 위에 보통 `@Override` 애노테이션을 붙입니다.

```java
@Override
public String toString() {
    return "Member";
}
```

`@Override`는 필수는 아니지만, 컴파일러가 "정말 override가 맞는지" 검사해 주기 때문에 붙이는 것이 좋습니다.

## 2. 왜 필요한가

프레임워크는 모든 프로젝트의 요구사항을 미리 알 수 없습니다.
그래서 기본 동작을 제공하면서도, 필요한 지점에서는 사용자가 동작을 바꿀 수 있도록 확장 지점을 열어둡니다.

Override는 이런 확장 지점을 사용하는 대표적인 방법입니다.

예를 들어 Spring MVC는 `ResponseEntityExceptionHandler`에서 framework 예외를 처리하는 기본 메서드를 제공합니다.
프로젝트는 이 메서드들을 override해서 Spring 기본 응답 대신 프로젝트의 공통 error envelope을 반환할 수 있습니다.

즉, override는 다음 상황에서 유용합니다.

- 부모 클래스의 기본 동작을 프로젝트 정책에 맞게 바꾸고 싶을 때
- 프레임워크가 제공한 확장 지점을 사용하고 싶을 때
- 메서드 이름과 호출 흐름은 유지하되 내부 구현만 바꾸고 싶을 때

## 3. 동작 조건

Java에서 method override가 되려면 기본적으로 다음 조건을 만족해야 합니다.

- 부모 클래스 또는 인터페이스에 같은 메서드가 있어야 합니다.
- 메서드 이름이 같아야 합니다.
- 파라미터 목록이 같아야 합니다.
- 반환 타입은 같거나 공변 반환 타입이어야 합니다.
- 접근 제어자는 부모 메서드보다 더 좁아질 수 없습니다.

예를 들어 부모 메서드가 `protected`라면 자식 메서드는 `protected` 또는 `public`으로 override할 수 있습니다.
하지만 `private`으로 줄일 수는 없습니다.

## 4. `@Override` 애노테이션의 역할

`@Override`는 "이 메서드는 부모 타입의 메서드를 재정의한다"는 의도를 컴파일러에게 알려줍니다.

장점은 다음과 같습니다.

- 메서드 이름 오타를 컴파일 시점에 잡을 수 있습니다.
- 파라미터 타입이나 개수가 달라 override가 되지 않는 문제를 잡을 수 있습니다.
- 코드를 읽는 사람이 이 메서드가 확장 지점이라는 것을 바로 알 수 있습니다.

예를 들어 부모 메서드 이름이 `handleTypeMismatch`인데 실수로 `handleTypeMisMatch`처럼 작성하면
`@Override`가 있을 때 컴파일 오류가 발생합니다.
`@Override`가 없으면 새로운 메서드를 하나 만든 것으로 취급될 수 있어 의도한 동작이 실행되지 않을 수 있습니다.

## 5. overload와의 차이

override와 overload는 이름이 비슷하지만 다릅니다.

| 구분 | 의미 |
| --- | --- |
| override | 부모의 메서드를 자식이 다시 구현 |
| overload | 같은 클래스 안에서 같은 이름의 메서드를 파라미터만 다르게 여러 개 정의 |

예를 들어 아래는 override입니다.

```java
class Child extends Parent {
    @Override
    void run() {
        // 부모의 run을 재정의
    }
}
```

아래는 overload입니다.

```java
class Printer {
    void print(String value) {
    }

    void print(int value) {
    }
}
```

프레임워크 메서드를 바꾸려는 경우에는 overload가 아니라 override가 되어야 합니다.
파라미터 타입이 조금이라도 다르면 Spring이 기대하는 메서드를 재정의하지 못할 수 있습니다.

## 6. 프로젝트 사용 방식

본 프로젝트에서는 `GlobalExceptionHandler`가 `ResponseEntityExceptionHandler`를 상속하고,
Spring MVC framework 예외 처리 메서드를 override합니다.

예:

```java
@Override
protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
) {
    // 프로젝트 공통 에러 응답으로 변환
}
```

이 메서드는 Spring MVC가 405 Method Not Allowed 상황에서 호출하는 확장 지점입니다.
프로젝트는 이 메서드를 override하여 기본 Spring 응답 대신 `ApiEnvelope` 기반 응답을 반환합니다.

다만 override 메서드가 Spring이 넘겨준 인자를 무시하면 문제가 생길 수 있습니다.
예를 들어 `headers`에는 405 응답에 필요한 `Allow` 헤더가 들어 있을 수 있습니다.
따라서 body를 공통 envelope으로 바꾸더라도 framework가 제공한 protocol header는 보존해야 합니다.

## 7. 주의사항

override를 사용할 때는 다음을 주의해야 합니다.

- `@Override`를 붙여 컴파일러 검증을 받습니다.
- 부모 메서드의 시그니처와 정확히 맞는지 확인합니다.
- 프레임워크가 넘겨준 파라미터를 무시해도 되는지 확인합니다.
- 부모 기본 동작이 제공하던 중요한 부수 효과를 잃지 않도록 확인합니다.
- Spring 버전이 바뀌면 override 메서드 시그니처가 달라졌는지 확인합니다.

특히 `ResponseEntityExceptionHandler`를 override할 때는 `headers`, `status`, `request` 같은 파라미터가
단순한 형식 맞추기용 값이 아닐 수 있습니다.
Spring이 이미 계산한 HTTP 응답 정보가 들어 있을 수 있으므로, 필요한 값은 최종 응답에 보존해야 합니다.

## 8. 프로젝트 관련 문서와 코드

- `src/main/java/com/mycom/springsandbox/common/handler/GlobalExceptionHandler.java`
- `docs/common/v1/adr/ADR-005-global-exception-handler.md`
- `docs/common/v1/theory/spring/exception/ResponseEntityExceptionHandler.md`
