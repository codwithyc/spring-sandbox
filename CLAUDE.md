# spring-sandbox

Spring 백엔드 개념·설계 판단을 코드·테스트·문서로 검증하며 쌓는 **개인 학습 레포**다. 완성형 서비스가 목표가 아니다(`README.md`).

## 현재 기준선 (2026-10 재시작)

- dev 에는 **common 만** 있다 — 이걸 기준으로 기능을 처음부터 다시 쌓는다.
  - `common/api` 공통 응답 `ApiEnvelope`(success · data · error · meta) / `ApiEnvelopes`(ResponseEntity 조립) / `ApiMeta`(requestId · UTC timestamp)
  - `common/error` · `common/enums` 에러 모델 `ApiError` · `FieldErrorItem` · `ErrorCodeSpec` · `ErrorCode`(COM-4xxx/5xxx)
  - `common/exception` · `common/handler` `BusinessException` 계층 + `GlobalExceptionHandler`(`ResponseEntityExceptionHandler` 기반)
  - `common/web` · `common/config` `RequestIdFilter`(X-Request-Id) · `TimeConfig`(`Clock`)
- 사용자 도메인 초안(#25, PR #33)은 버렸다. 이슈 #23~#32 는 닫았고 새 기능은 새 이슈로 연다.

## 스택 · 명령

- Java 17 · Spring Boot 3.5.3 · Gradle · Lombok. **JPA·DB 는 아직 없다** — 필요해지는 이슈에서 추가한다.
- 테스트: `./gradlew test` (dev 에서 PR 시 GitHub Actions 가 같은 걸 돌린다)
- 실행: `./gradlew bootRun` (8080)

## 규칙 — 원본은 `docs/common/v1/policy/`

| 문서 | 요점 |
|---|---|
| `BRANCH_STRATEGY.md` | 이슈 먼저 → `<prefix>/<issue>-<desc>` 브랜치 → dev 로 PR(squash). main·dev 직접 커밋 금지 |
| `COMMIT_CONVENTION.md` | gitmoji 1개 + 한국어 제목, 본문은 `.gitmessage.txt`(변경 이유 / 주요 변경 사항 / 비고) |
| `FOLDERING_POLICY.md` | 도메인 패키지 + 내부 레이어(api/application/domain/infrastructure), 공통은 `common` |
| `NAMING_CONVENTION.md` · `ADR_POLICY.md` | 이름 규칙 · 설계 결정은 `docs/common/v1/adr/ADR-NNN-*.md` |
| `THEORY_DOCUMENT_TEMPLATE.md` | 새로 다룬 개념은 `docs/common/v1/theory/` 에 이 템플릿으로 |

- 새 API 는 컨트롤러에서 `ApiEnvelopes` 로 응답하고, 실패는 `BusinessException` 계층을 던진다(응답을 직접 조립하지 않는다).
- 에러 코드를 추가할 때는 ADR-003(에러 코드 관리)을 따른다.
