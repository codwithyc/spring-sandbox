# 커밋 메시지 컨벤션

## 📌 개요
spring-sandbox 프로젝트는 **Conventional Commits** 스펙을 기반으로 하며, Jira 티켓 번호를 포함하는 커밋 메시지 컨벤션을 사용합니다.

## 📝 커밋 메시지 구조

```
<type>(<scope>): <JIRA-ID> <subject>

<body>

<footer>
```

### 구성 요소

#### 1. Type (필수)
커밋의 종류를 나타냅니다.

| Type | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 추가 | 사용자 인증 기능 |
| `fix` | 버그 수정 | NPE 오류 수정 |
| `docs` | 문서 변경 | README 업데이트 |
| `style` | 코드 포맷팅 (기능 변경 없음) | 코드 정렬, 세미콜론 추가 |
| `refactor` | 코드 리팩토링 | 함수 분리, 구조 개선 |
| `test` | 테스트 코드 추가/수정 | 단위 테스트 추가 |
| `chore` | 빌드/설정 변경 | Gradle 의존성 업데이트 |
| `perf` | 성능 개선 | 쿼리 최적화 |
| `ci` | CI/CD 설정 변경 | GitHub Actions 워크플로우 수정 |
| `revert` | 커밋 되돌리기 | 이전 커밋 취소 |

#### 2. Scope (선택)
변경 범위를 나타냅니다.

예시:
- `auth`: 인증/인가
- `api`: API 엔드포인트
- `db`: 데이터베이스
- `config`: 설정
- `user`: 사용자 관련
- `order`: 주문 관련

#### 3. JIRA-ID (필수)
연관된 Jira 티켓 번호를 명시합니다.

형식: `PROJ-123` (프로젝트 키-티켓 번호)

#### 4. Subject (필수)
변경 사항을 간결하게 요약합니다.

규칙:
- 50자 이내
- 명령형 현재 시제 사용
- 마침표 사용 안 함
- 한글 또는 영어 사용

#### 5. Body (선택)
상세한 변경 내용을 설명합니다.

규칙:
- 72자마다 줄바꿈
- 무엇을, 왜 변경했는지 설명
- 어떻게 변경했는지는 코드를 보면 알 수 있으므로 생략 가능

#### 6. Footer (선택)
이슈 트래커 참조, Breaking Changes 등을 명시합니다.

예시:
- `Closes #123`: 이슈 종료
- `Refs #456`: 이슈 참조
- `BREAKING CHANGE: ...`: 하위 호환성 깨짐

## ✅ 커밋 메시지 예시

### 기본 예시

```
feat(auth): PROJ-123 add user login functionality
```

### Scope 포함

```
fix(api): PROJ-456 resolve null pointer exception in user endpoint
```

### Body 포함

```
feat(order): PROJ-789 implement order processing logic

- Add order validation
- Integrate payment gateway
- Send confirmation email
```

### Footer 포함

```
fix(db): PROJ-321 optimize user query performance

Improve query execution time from 2s to 200ms by adding index.

Closes #321
```

### Breaking Change

```
refactor(api): PROJ-555 restructure API response format

BREAKING CHANGE: API response structure changed from flat to nested.
Clients need to update their integration code.
```

## 🔗 Jira 연동

### 자동 연동 트리거
커밋 메시지에 Jira 티켓 번호를 포함하면 자동으로 연동됩니다:

1. **커밋 참조**: `PROJ-123`이 포함된 커밋 → Jira 티켓에 자동 링크
2. **PR 연동**: PR 제목에 티켓 번호 포함 → Jira에서 PR 상태 확인 가능
3. **자동 전환**: 특정 키워드 사용 시 티켓 상태 자동 변경

### Smart Commits
Jira Smart Commits를 활용하여 커밋으로 이슈 상태를 변경할 수 있습니다:

```
feat(auth): PROJ-123 add login feature #done

# Jira 티켓 PROJ-123의 상태를 "Done"으로 변경
```

```
fix(api): PROJ-456 fix bug #time 2h #comment Fixed the null pointer issue

# Jira 티켓에 작업 시간 2시간 기록 및 코멘트 추가
```

### Smart Commits 키워드
- `#comment`: 코멘트 추가
- `#time`: 작업 시간 기록
- `#done`: 이슈를 완료 상태로 전환
- `#in-progress`: 이슈를 진행 중 상태로 전환

## 🛠️ Git Hooks 설정 (선택)

커밋 메시지 형식을 자동으로 검증하기 위해 Git Hooks를 설정할 수 있습니다.

### commitlint 설정

```bash
# npm 패키지 설치 (프로젝트 루트에서)
npm install --save-dev @commitlint/cli @commitlint/config-conventional

# commitlint 설정 파일 생성
echo "module.exports = {extends: ['@commitlint/config-conventional']}" > commitlint.config.js

# husky 설치 (Git Hooks 관리)
npm install --save-dev husky
npx husky install
npx husky add .husky/commit-msg 'npx --no -- commitlint --edit "$1"'
```

## ✅ 체크리스트

### 커밋하기 전
- [ ] Type이 적절한가?
- [ ] Jira 티켓 번호가 포함되었는가?
- [ ] Subject가 명확하고 간결한가?
- [ ] 변경 사항이 논리적으로 분리되었는가?

### PR 생성 전
- [ ] 커밋 메시지가 일관성 있는가?
- [ ] 관련 없는 변경사항이 섞여있지 않은가?
- [ ] 테스트가 통과하는가?

## 📚 참고 자료

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Jira Smart Commits](https://support.atlassian.com/jira-software-cloud/docs/process-issues-with-smart-commits/)
- [Git Flow 설정 가이드](./GIT_FLOW_SETUP.md)

## 🔍 잘못된 예시와 올바른 예시

### ❌ 잘못된 예시

```
# Jira 티켓 번호 없음
feat: add login feature

# Type 누락
PROJ-123 add login feature

# 의미 없는 메시지
fix: fix bug

# 너무 긴 subject
feat(auth): PROJ-123 add user authentication feature with email verification and password reset functionality
```

### ✅ 올바른 예시

```
feat(auth): PROJ-123 add user login functionality

fix(api): PROJ-456 resolve NPE in user endpoint

docs: PROJ-789 update README with setup instructions

refactor(service): PROJ-111 extract validation logic to separate class
```

---
*최종 업데이트: 2026-01-28*
