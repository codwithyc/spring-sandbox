# 공통 문서 (Common Documentation)

## 📚 문서 목록

이 디렉토리는 프로젝트의 공통 개발 프로세스와 규칙에 관한 문서를 포함합니다.

### 개발 프로세스

| 문서 | 설명 | 대상 |
|------|------|------|
| [BRANCH_STRATEGY.md](./BRANCH_STRATEGY.md) | 브랜치 전략 개요 및 Gitflow 워크플로우 | 전체 개발자 |
| [GIT_FLOW_SETUP.md](./GIT_FLOW_SETUP.md) | Git Flow 초기 설정 및 상세 워크플로우 | 프로젝트 리더, 전체 개발자 |
| [GITHUB_SETTINGS.md](./GITHUB_SETTINGS.md) | GitHub 저장소 설정 가이드 (브랜치 보호 등) | 저장소 관리자 |

### 코드 품질

| 문서 | 설명 | 대상 |
|------|------|------|
| [COMMIT_CONVENTION.md](./COMMIT_CONVENTION.md) | 커밋 메시지 컨벤션 (Conventional Commits) | 전체 개발자 |
| [EXEPTION_HANDLING.md](./EXEPTION_HANDLING.md) | 예외 처리 가이드 | 전체 개발자 |

### 통합 및 자동화

| 문서 | 설명 | 대상 |
|------|------|------|
| [JIRA_INTEGRATION.md](./JIRA_INTEGRATION.md) | Jira-GitHub 연동 설정 및 사용법 | 프로젝트 리더, 전체 개발자 |

---

## 🚀 빠른 시작

### 신규 개발자 온보딩
1. [BRANCH_STRATEGY.md](./BRANCH_STRATEGY.md) - 브랜치 전략 이해
2. [COMMIT_CONVENTION.md](./COMMIT_CONVENTION.md) - 커밋 메시지 작성법 학습
3. [GIT_FLOW_SETUP.md](./GIT_FLOW_SETUP.md) - 개발 워크플로우 실습

### 프로젝트 관리자
1. [GITHUB_SETTINGS.md](./GITHUB_SETTINGS.md) - GitHub 저장소 초기 설정
2. [JIRA_INTEGRATION.md](./JIRA_INTEGRATION.md) - Jira 연동 설정
3. [GIT_FLOW_SETUP.md](./GIT_FLOW_SETUP.md) - Git Flow 환경 구축

---

## 📖 주요 규칙 요약

### 브랜치 네이밍
```
feature/{JIRA-ID}-{description}   # 예: feature/PROJ-123-user-login
release/v{major}.{minor}.{patch}  # 예: release/v1.0.0
hotfix/{JIRA-ID}-{description}    # 예: hotfix/PROJ-456-critical-fix
```

### 커밋 메시지
```
<type>(<scope>): <JIRA-ID> <subject>

# 예시
feat(auth): PROJ-123 implement user authentication
fix(api): PROJ-456 resolve null pointer exception
docs: PROJ-789 update README with setup guide
```

### PR 워크플로우
1. `develop`에서 feature 브랜치 생성
2. 기능 개발 및 커밋
3. `develop`으로 PR 생성 (Jira 티켓 번호 포함)
4. 코드 리뷰 및 CI 통과
5. `develop`에 머지

---

## 🔄 문서 업데이트

문서 수정이 필요한 경우:
1. 이슈 생성하여 변경 사항 논의
2. feature 브랜치에서 문서 수정
3. PR 생성 및 리뷰
4. 승인 후 머지

---

*최종 업데이트: 2026-01-28*
