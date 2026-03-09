# FOLDERING_POLICY

## 1. 목적
이 문서는 `src/main/java` 기준 프로젝트 폴더(패키지) 구조 표준을 정의한다.
목표는 다음과 같다.

- 도메인 중심으로 코드 응집도 향상
- 계층 책임 분리로 변경 영향 최소화
- 팀 확장 시 파일 위치와 의존 방향을 일관되게 유지

## 2. 기본 전략
- 기본 전략은 **도메인 중심(package-by-domain)** 으로 구성한다.
- 각 도메인 내부는 **레이어 분리(api/application/domain/infrastructure)** 를 적용한다.
- 공통 기술/정책성 코드는 `common` 패키지에 둔다.

## 3. 표준 구조

```text
src/main/java/com/mycom/springsandbox
├─ common
│  ├─ api
│  ├─ config
│  ├─ error
│  ├─ exception
│  ├─ handler
│  └─ web
├─ order
│  ├─ api
│  ├─ application
│  ├─ domain
│  └─ infrastructure
├─ user
│  ├─ api
│  ├─ application
│  ├─ domain
│  └─ infrastructure
└─ auth
   ├─ api
   ├─ application
   ├─ domain
   └─ infrastructure
```

## 4. 레이어 책임

### 4.1 `api`
- Controller, Request DTO, Response DTO
- HTTP 입출력 변환 및 검증 진입점
- 비즈니스 규칙 직접 구현 금지

### 4.2 `application`
- UseCase/Service, Command/Result
- 트랜잭션 경계, 유스케이스 오케스트레이션
- 외부 입력 DTO를 내부 명령 객체로 변환

### 4.3 `domain`
- Entity, Value Object, Domain Service, Domain Policy
- 핵심 비즈니스 규칙 보유
- 프레임워크 의존 최소화 (Spring/JPA 애노테이션 의존 최소화 지향)

### 4.4 `infra`
- JPA Repository 구현체, 외부 API/메시지 브로커 연동
- 영속성/외부 시스템 세부 구현
- 기술 의존을 도메인 바깥으로 격리

## 5. `common` 패키지 운영 기준
- 도메인 비즈니스 로직은 `common`에 두지 않는다.
- 다음과 같은 전역 횡단 관심사만 허용한다.
  - 공통 응답 포맷 (`ApiEnvelope`, `ApiError`)
  - 전역 예외 처리 (`GlobalExceptionHandler`)
  - 요청 추적/필터 (`RequestIdFilter`)
  - 전역 설정 (`config`)

## 6. 의존 방향 규칙
- `api` -> `application` 의존 허용
- `application` -> `domain` 의존 허용
- `infrastructure` -> `domain` 의존 허용
- `domain` -> (`api`, `application`, `infrastructure`) 의존 금지
- 도메인 간 직접 참조는 최소화하고, 필요 시 `application` 계층에서 조정한다.

## 7. 패키지 생성 규칙
- 새 기능은 먼저 도메인 경계(`order`, `user` 등)를 정한 뒤 패키지를 생성한다.
- 기능이 작아도 장기 확장을 고려해 레이어 이름을 유지한다.
- 단일 파일 유틸성 코드는 무분별한 `util` 생성보다 명확한 책임 패키지에 배치한다.

## 8. 테스트 폴더 매핑 규칙
- 테스트 패키지는 운영 코드 경로를 그대로 미러링한다.
- 예:
  - `src/main/java/com/mycom/springsandbox/order/application/...`
  - `src/test/java/com/mycom/springsandbox/order/application/...`

## 9. 금지 규칙
- `controller`, `service`, `repository`를 전역 루트로 두는 레이어 단일 구조 금지
- 도메인과 무관한 공통 코드의 과도한 `common` 집중 금지
- 패키지 명에 약어/모호한 이름(`etc`, `misc`, `temp`) 사용 금지

## 10. 변경 규칙
- 폴더 구조 정책 변경은 `FOLDERING_POLICY.md`를 먼저 수정한다.
- 대규모 패키지 이동은 별도 커밋으로 분리한다.
- 구조 변경 시 관련 문서(ADR, 정책 문서, README)의 경로 참조를 함께 갱신한다.
