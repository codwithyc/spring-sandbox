# Git Flow 설정 가이드

## 📌 개요
이 문서는 spring-sandbox 프로젝트의 Git Flow 전략 설정 및 관리 방법을 설명합니다.

## 🌿 브랜치 구조

### 주요 브랜치
- **main**: 프로덕션 배포 브랜치 (태그 기반 배포)
- **develop**: 개발 통합 브랜치 (스테이징 환경)

### 보조 브랜치
- **feature/**: 새로운 기능 개발
- **release/**: 릴리즈 준비 및 QA
- **hotfix/**: 긴급 버그 수정

## 🔧 초기 설정

### 1. develop 브랜치 생성

```bash
# main 브랜치에서 develop 브랜치 생성
git checkout main
git pull origin main
git checkout -b develop
git push -u origin develop
```

### 2. GitHub 저장소 설정 (관리자 권한 필요)

#### 2.1 기본 브랜치 변경
1. GitHub 저장소 페이지 → Settings → Branches
2. Default branch를 `main`에서 `develop`으로 변경
3. Update 클릭

#### 2.2 main 브랜치 보호 규칙 설정
1. GitHub 저장소 페이지 → Settings → Branches
2. "Add branch protection rule" 클릭
3. Branch name pattern: `main`
4. 다음 옵션 활성화:
   - ✅ Require a pull request before merging
   - ✅ Require approvals (최소 1명)
   - ✅ Require status checks to pass before merging
   - ✅ Require conversation resolution before merging
   - ✅ Do not allow bypassing the above settings
5. Create 클릭

#### 2.3 develop 브랜치 보호 규칙 설정
1. 위와 동일한 방법으로 `develop` 브랜치에 대한 보호 규칙 추가
2. Branch name pattern: `develop`
3. 동일한 보호 옵션 적용

## 📋 워크플로우

### Feature 개발 워크플로우

```bash
# 1. develop 브랜치에서 feature 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/PROJ-123-user-authentication

# 2. 기능 개발 및 커밋
git add .
git commit -m "feat(auth): PROJ-123 implement user authentication"

# 3. 원격 저장소에 푸시
git push -u origin feature/PROJ-123-user-authentication

# 4. GitHub에서 develop 브랜치로 Pull Request 생성
# 5. 코드 리뷰 및 승인
# 6. develop 브랜치로 머지
```

### Release 워크플로우

```bash
# 1. develop에서 release 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# 2. 버전 업데이트 및 QA
# build.gradle 등에서 버전 업데이트
git commit -m "chore(release): bump version to 1.0.0"

# 3. QA 완료 후 main으로 머지
git checkout main
git pull origin main
git merge --no-ff release/v1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# 4. develop에도 변경사항 머지
git checkout develop
git merge --no-ff release/v1.0.0
git push origin develop

# 5. release 브랜치 삭제
git branch -d release/v1.0.0
```

### Hotfix 워크플로우

```bash
# 1. main에서 hotfix 브랜치 생성
git checkout main
git pull origin main
git checkout -b hotfix/PROJ-456-critical-bug

# 2. 버그 수정
git add .
git commit -m "fix(critical): PROJ-456 resolve critical bug"

# 3. main으로 머지
git checkout main
git merge --no-ff hotfix/PROJ-456-critical-bug
git tag -a v1.0.1 -m "Hotfix version 1.0.1"
git push origin main --tags

# 4. develop에도 머지
git checkout develop
git merge --no-ff hotfix/PROJ-456-critical-bug
git push origin develop

# 5. hotfix 브랜치 삭제
git branch -d hotfix/PROJ-456-critical-bug
```

## 🔖 브랜치 네이밍 규칙

| 브랜치 타입 | 패턴 | 예시 |
|-----------|------|------|
| Feature | `feature/{JIRA-ID}-{description}` | `feature/PROJ-123-user-login` |
| Release | `release/v{major}.{minor}.{patch}` | `release/v1.2.0` |
| Hotfix | `hotfix/{JIRA-ID}-{description}` | `hotfix/PROJ-456-security-fix` |

### 네이밍 가이드라인
- 모두 소문자 사용
- 단어 구분은 하이픈(-) 사용
- Jira 티켓 ID는 반드시 포함
- 간결하고 명확한 설명 사용

## ✅ 체크리스트

### 초기 설정
- [ ] develop 브랜치 생성 및 푸시
- [ ] GitHub에서 기본 브랜치를 develop으로 변경
- [ ] main 브랜치 보호 규칙 설정
- [ ] develop 브랜치 보호 규칙 설정

### 팀 규칙 합의
- [ ] 커밋 메시지 컨벤션 합의
- [ ] 브랜치 네이밍 규칙 합의
- [ ] PR 리뷰 프로세스 정의
- [ ] CI/CD 파이프라인 확인

## 📚 참고 문서
- [커밋 메시지 컨벤션](./COMMIT_CONVENTION.md)
- [Jira 연동 가이드](./JIRA_INTEGRATION.md)
- [브랜치 전략](./BRANCH_STRATEGY.md)

## 🔍 트러블슈팅

### 문제: develop 브랜치가 이미 존재함
```bash
# 로컬 develop 브랜치 삭제
git branch -D develop

# 원격 develop 브랜치에서 새로 받기
git checkout -b develop origin/develop
```

### 문제: 보호 규칙으로 인해 푸시 불가
- 관리자에게 권한 요청
- 또는 Pull Request를 통해 변경사항 머지

---
*최종 업데이트: 2026-01-28*
