# JsonInclude

## 1. JsonInclude란?
`@JsonInclude`는 Jackson 직렬화 시 필드 포함 정책을 제어하는 어노테이션이다.  
주로 `null`/빈 값 필드를 응답 JSON에 포함할지 결정할 때 사용한다.

## 2. 왜 사용하는가?
- 응답 크기 최적화
- 불필요한 필드 제거
- API 계약(응답 키 고정 여부) 정책 반영

## 3. Include 옵션
- `ALWAYS`: 항상 포함 (`null` 포함)
- `NON_NULL`: `null` 제외
- `NON_ABSENT`: `null`, `Optional.empty()` 제외
- `NON_EMPTY`: `null`, 빈 문자열, 빈 컬렉션/배열 제외
- `NON_DEFAULT`: 기본값(0/false 등) 제외
- `CUSTOM`: 커스텀 필터 기준
- `USE_DEFAULTS`: 전역/기본 설정 사용

## 4. 프로젝트 사용 구간
- `ApiEnvelope`: 외부 응답 계약(`success/data/error/meta`)을 표현하는 최상위 응답 객체
- `ApiError` 등 내부 상세 DTO: 필요 시 `NON_NULL`/`NON_EMPTY` 선택 적용 가능

## 5. 프로젝트 정책 (중요)
- 우리 프로젝트는 외부 계약 불변성을 위해 `ApiEnvelope`에 `@JsonInclude(JsonInclude.Include.ALWAYS)`를 사용한다.
- 따라서 성공/실패 모두 `data`, `error` 키는 유지되고 값만 `null`일 수 있다.
- 전역 설정(`spring.jackson.default-property-inclusion`)이 `NON_NULL`이어도, `ApiEnvelope`는 클래스 레벨에서 `ALWAYS`를 명시한다.
- `NON_NULL`, `NON_EMPTY`는 계약 고정이 필요 없는 내부 DTO에서만 제한적으로 사용한다.

## 6. 주의사항
- `ApiEnvelope`에 `NON_NULL`을 적용하면 `data` 또는 `error` 키가 사라져 계약 불변성이 깨질 수 있다.
- `JsonInclude`는 직렬화 포함 정책이지, 보안 정책 자체가 아니다.
- 민감정보 노출 제어는 `@JsonIgnore`, DTO 분리, 마스킹 정책으로 별도 처리한다.

## 7. 예시

### 7.1 ApiEnvelope (권장)
```java
@JsonInclude(JsonInclude.Include.ALWAYS)
public record ApiEnvelope<T>(
    boolean success,
    T data,
    ApiError error,
    ApiMeta meta
) {}
```

### 7.2 내부 DTO (선택)
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
    String code,
    Integer status,
    String message
) {}
```

## 8. 참고자료
- [Jackson JsonInclude 공식 문서](https://fasterxml.github.io/jackson-annotations/javadoc/2.9/com/fasterxml/jackson/annotation/JsonInclude.html)
