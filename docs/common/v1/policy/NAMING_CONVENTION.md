# NAMING_CONVENTION

## 1. 목적
이 문서는 `docs/common/v1` 문서 체계에서 파일/폴더 네이밍 규칙을 표준화한다.
목표는 다음과 같다.

- 문서 성격(정책/이론/수정 기록)을 이름만으로 즉시 구분
- 파일 검색성 향상 (`rg`, IDE 검색, Git 이력 추적)
- 팀 확장 시 충돌 최소화

## 2. 기본 원칙
- 디렉토리는 역할 중심으로 분리하고, 파일 이름은 문서 성격을 반영한다.
- 동일 성격 문서는 동일 케이스를 사용한다.
- 약어/오타를 허용하지 않는다.

## 3. 디렉토리 네이밍 규칙
- 디렉토리 이름은 소문자 `kebab-case`를 사용한다.
- 버전 디렉토리는 `v{major}` 형식을 사용한다. 예: `v1`, `v2`
- 권장 최상위 구조:

```text
docs/common/v1/
  policy/
  theory/
  troubleshooting/ (필요 시)
  adr/ (필요 시)
```

## 4. 파일 네이밍 규칙

### 4.1 정책 문서
- 형식: `UPPER_SNAKE_CASE.md`
- 예시:
  - `NAMING_CONVENTION.md`
  - `FOLDERING_POLICY.md`
  - `ADR_POLICY.md`
  - `COMMIT_CONVENTION.md`
  - `BRANCH_STRATEGY.md`
  - `EXCEPTION_HANDLING.md`

### 4.2 이론 문서
- 형식: `PascalCase.md`
- 예시:
  - `JsonInclude.md`
  - `IllegalArgumentException.md`
  - `NullPointerException.md`

### 4.3 템플릿 문서
- 팀 표준 템플릿은 정책 성격으로 간주한다.
- 형식: `UPPER_SNAKE_CASE.md`
- 예시: `THEORY_DOCUMENT_TEMPLATE.md`

### 4.4 ADR 문서
- 형식: `ADR-번호-kebab-case.md`
- 예시: `ADR-001-response-envelope.md`

## 5. 금지 규칙
- 오타 파일명 금지 (예: `EXEPTION` 금지, `EXCEPTION` 사용)
- 동일 성격 문서에 케이스 혼용 금지
- 공백/한글 파일명 금지 (검색/호환성 이슈 방지)

## 6. 변경 규칙
- 네이밍 규칙 변경 시 `NAMING_CONVENTION.md`를 먼저 수정한다.
- 기존 파일명 변경 시, 관련 링크/참고 문서를 함께 갱신한다.
- 대규모 리네임은 별도 커밋으로 분리한다.

## 7. 현재 적용 상태 (v1)
- 정책 문서: `UPPER_SNAKE_CASE` 적용
- 이론 문서: `PascalCase` 적용
- 기존 오타 문서 `EXEPTION_HANDLING.md`는 `EXCEPTION_HANDLING.md`로 정정
- ADR 상세 규칙은 `ADR_POLICY.md`를 기준으로 관리
