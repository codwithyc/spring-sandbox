# Jira - GitHub 연동 가이드

## 📌 개요
이 문서는 Jira와 GitHub을 연동하여 이슈 추적과 코드 변경 사항을 통합 관리하는 방법을 설명합니다.

## 🔗 연동 방법

### 1. Jira GitHub 앱 설치 (관리자 권한 필요)

#### 1.1 GitHub Marketplace에서 Jira 앱 설치
1. GitHub Marketplace 방문: https://github.com/marketplace/jira-software-github
2. "Set up a plan" 클릭
3. 연동할 조직/계정 선택
4. "Install it for free" 클릭
5. "Complete order and begin installation" 클릭
6. 연동할 저장소 선택
   - All repositories (모든 저장소)
   - Only select repositories (특정 저장소만)
7. "Install" 클릭

#### 1.2 Jira에서 GitHub 연결
1. Jira 관리 페이지 → Apps → Manage your apps
2. "GitHub for Jira" 찾기
3. "Get started" 클릭
4. GitHub 계정으로 인증
5. 연동할 조직 및 저장소 선택
6. "Authorize" 클릭

### 2. 권한 설정

#### GitHub 저장소 권한
Jira GitHub 앱에 다음 권한이 필요합니다:
- ✅ Read access to code
- ✅ Read and write access to issues
- ✅ Read and write access to pull requests
- ✅ Read access to metadata

#### Jira 프로젝트 권한
연동을 위해 Jira 프로젝트 관리자 권한이 필요합니다.

## 📋 연동 기능

### 1. 커밋 연동
커밋 메시지에 Jira 티켓 번호를 포함하면 자동으로 연동됩니다.

```bash
# 커밋 메시지에 티켓 번호 포함
git commit -m "feat(auth): PROJ-123 implement user authentication"
```

**Jira에서 확인**:
- Jira 티켓 → Development 섹션
- 연결된 커밋 정보 표시
- GitHub 커밋으로 직접 이동 가능

### 2. 브랜치 연동
브랜치 이름에 Jira 티켓 번호를 포함합니다.

```bash
# 브랜치 생성
git checkout -b feature/PROJ-123-user-authentication
git push -u origin feature/PROJ-123-user-authentication
```

**Jira에서 확인**:
- Jira 티켓 → Development 섹션
- 연결된 브랜치 정보 표시
- 브랜치 상태 확인 가능

### 3. Pull Request 연동
PR 제목이나 본문에 Jira 티켓 번호를 포함합니다.

```
Title: feat(auth): PROJ-123 implement user authentication

Description:
- Implement login endpoint
- Add JWT token generation
- Update user model

Jira: PROJ-123
```

**Jira에서 확인**:
- Jira 티켓 → Development 섹션
- PR 상태 (Open, Merged, Closed)
- PR 리뷰 상태
- CI/CD 빌드 상태

### 4. 이슈 자동 전환
PR이 머지되면 Jira 티켓 상태를 자동으로 변경할 수 있습니다.

## 🎯 Smart Commits

Smart Commits를 사용하여 커밋으로 Jira 이슈를 제어할 수 있습니다.

### 기본 문법
```
<JIRA-ID> #<command> <arguments>
```

### 지원 명령어

#### 1. 코멘트 추가
```bash
git commit -m "PROJ-123 #comment Implemented login logic"
```

#### 2. 작업 시간 기록
```bash
git commit -m "PROJ-123 #time 2h 30m Fixed authentication bug"
```

#### 3. 이슈 상태 전환
```bash
git commit -m "PROJ-123 #done Completed user authentication"
```

#### 4. 복합 명령
```bash
git commit -m "PROJ-123 #time 1h #comment Refactored auth service #done"
```

### Smart Commits 예시

```bash
# 작업 시작
git commit -m "feat(auth): PROJ-123 start authentication #in-progress"

# 작업 진행 중
git commit -m "feat(auth): PROJ-123 implement login endpoint #time 2h #comment Added JWT support"

# 작업 완료
git commit -m "feat(auth): PROJ-123 complete authentication #done #time 1h"
```

## 🔄 워크플로우 연동

### Feature 개발 워크플로우

```
1. Jira에서 이슈 생성 (PROJ-123)
   ↓
2. 브랜치 생성: feature/PROJ-123-user-auth
   ↓ (자동 연동: Jira에 브랜치 표시)
3. 개발 및 커밋: "feat(auth): PROJ-123 add login"
   ↓ (자동 연동: Jira에 커밋 표시)
4. PR 생성: "feat(auth): PROJ-123 implement auth"
   ↓ (자동 연동: Jira에 PR 표시)
5. PR 머지
   ↓ (자동 전환: Jira 이슈 상태 변경 가능)
6. Jira 이슈 완료
```

## 📊 Jira 패널 설정

### Development Panel 활성화
1. Jira 프로젝트 → 프로젝트 설정
2. Features → Development tools
3. "Enable development tools" 활성화
4. GitHub 저장소 연결 확인

### 표시되는 정보
- **Branches**: 연결된 브랜치 목록
- **Commits**: 관련 커밋 내역
- **Pull Requests**: PR 상태 및 리뷰 정보
- **Builds**: CI/CD 빌드 결과

## 🛠️ PR 템플릿 업데이트

`.github/PULL_REQUEST_TEMPLATE.md` 파일에 Jira 티켓 필드 추가:

```markdown
## 📌 관련 Jira 티켓
<!-- Jira 티켓 번호를 입력하세요 (예: PROJ-123) -->
Jira: 

## 📌 관련 이슈
<!-- GitHub 이슈 번호를 입력하세요 (예: #123) -->
Closes #
```

## ✅ 연동 테스트

### 테스트 시나리오

1. **Jira 이슈 생성**
   ```
   - Summary: 사용자 로그인 기능 구현
   - Issue Type: Story
   - Key: PROJ-123
   ```

2. **브랜치 생성 및 푸시**
   ```bash
   git checkout -b feature/PROJ-123-user-login
   git push -u origin feature/PROJ-123-user-login
   ```

3. **커밋 작성**
   ```bash
   git commit -m "feat(auth): PROJ-123 implement login endpoint"
   git push
   ```

4. **Jira 확인**
   - Development 섹션에 브랜치 표시 확인
   - 커밋 링크 확인

5. **PR 생성**
   - 제목: `feat(auth): PROJ-123 implement user login`
   - 본문에 Jira 티켓 번호 포함

6. **Jira 확인**
   - PR 정보 표시 확인
   - PR 상태 확인

### 체크리스트
- [ ] GitHub Marketplace에서 Jira 앱 설치
- [ ] Jira에서 GitHub 저장소 연결
- [ ] Development Panel 활성화
- [ ] 브랜치 연동 테스트
- [ ] 커밋 연동 테스트
- [ ] PR 연동 테스트
- [ ] Smart Commits 테스트

## 🔍 트러블슈팅

### 문제: Jira에 GitHub 정보가 표시되지 않음
**원인**: 
- GitHub 앱 권한 부족
- 티켓 번호 형식 오류
- Development Panel 비활성화

**해결방법**:
1. GitHub 앱 권한 확인
2. 티켓 번호 형식 확인 (프로젝트키-번호)
3. Jira Development Panel 활성화 확인

### 문제: Smart Commits가 작동하지 않음
**원인**:
- Smart Commits 기능 비활성화
- 명령어 형식 오류

**해결방법**:
1. Jira 관리 → Apps → GitHub for Jira → Configuration
2. "Enable Smart Commits" 활성화
3. 명령어 형식 확인

### 문제: PR 상태가 업데이트되지 않음
**원인**:
- Webhook 설정 오류
- GitHub 앱 권한 부족

**해결방법**:
1. Jira → Apps → GitHub for Jira → Manage Repositories
2. 저장소 연결 상태 확인
3. Refresh 또는 재연결

## 📚 참고 자료

- [Jira GitHub Integration Official Docs](https://support.atlassian.com/jira-cloud-administration/docs/integrate-with-github/)
- [Smart Commits Documentation](https://support.atlassian.com/jira-software-cloud/docs/process-issues-with-smart-commits/)
- [커밋 메시지 컨벤션](./COMMIT_CONVENTION.md)
- [Git Flow 설정 가이드](./GIT_FLOW_SETUP.md)

## 📝 주요 규칙 요약

1. **브랜치 이름**: `feature/PROJ-123-description`
2. **커밋 메시지**: `type(scope): PROJ-123 subject`
3. **PR 제목**: `type(scope): PROJ-123 subject`
4. **Smart Commits**: `PROJ-123 #command arguments`

---
*최종 업데이트: 2026-01-28*
