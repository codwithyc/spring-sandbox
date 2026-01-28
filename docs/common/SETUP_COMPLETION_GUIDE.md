# Git Flow 및 Jira 연동 완료 가이드

## 📌 개요
이 문서는 이 PR이 머지된 후 Git Flow와 Jira 연동을 완료하기 위한 단계별 가이드입니다.

---

## ✅ 완료된 작업 (이 PR에 포함됨)

### 1. 문서화
- [x] Git Flow 초기 설정 가이드 (`docs/common/GIT_FLOW_SETUP.md`)
- [x] GitHub 저장소 설정 가이드 (`docs/common/GITHUB_SETTINGS.md`)
- [x] 커밋 메시지 컨벤션 (`docs/common/COMMIT_CONVENTION.md`)
- [x] Jira 연동 가이드 (`docs/common/JIRA_INTEGRATION.md`)
- [x] 문서 인덱스 (`docs/common/README.md`)

### 2. PR 템플릿 업데이트
- [x] Jira 티켓 번호 필드 추가

### 3. CI/CD 설정
- [x] CI 워크플로우에서 `develop` 브랜치 지원 추가

### 4. 테스트
- [x] Git Flow 테스트 예시 작성 (`docs/examples/GIT_FLOW_TEST_EXAMPLE.md`)
- [x] 빌드 및 테스트 검증 완료

---

## 🚀 다음 단계 (저장소 관리자가 수행)

### 1단계: develop 브랜치 생성 ⭐ 최우선

```bash
# 저장소를 로컬에 클론 (이미 있다면 스킵)
git clone https://github.com/codwithyc/spring-sandbox.git
cd spring-sandbox

# main 브랜치를 최신 상태로 업데이트
git checkout main
git pull origin main

# develop 브랜치 생성 및 푸시
git checkout -b develop
git push -u origin develop
```

**확인**: GitHub 저장소에서 `develop` 브랜치가 생성되었는지 확인

---

### 2단계: GitHub 저장소 기본 설정

#### 2-1. 기본 브랜치 변경
1. GitHub 저장소 페이지 방문: https://github.com/codwithyc/spring-sandbox
2. **Settings** 탭 클릭
3. 왼쪽 메뉴에서 **Branches** 클릭
4. **Default branch** 섹션에서 브랜치 전환 아이콘 클릭
5. `develop` 선택 → **Update** 클릭
6. 확인 다이얼로그에서 **I understand, update the default branch** 클릭

**확인**: 저장소 메인 페이지에서 기본 브랜치가 `develop`으로 표시되는지 확인

#### 2-2. main 브랜치 보호 규칙 설정
1. **Settings** → **Branches** 이동
2. **Add branch protection rule** 클릭
3. **Branch name pattern**: `main` 입력
4. 다음 옵션 활성화:
   - ✅ Require a pull request before merging
     - ✅ Require approvals: 1
   - ✅ Require status checks to pass before merging
     - Status checks: `build` 선택 (처음 CI 실행 후 표시됨)
   - ✅ Require conversation resolution before merging
   - ✅ Do not allow bypassing the above settings (권장)
5. **Create** 클릭

**상세 가이드**: `docs/common/GITHUB_SETTINGS.md` 참조

#### 2-3. develop 브랜치 보호 규칙 설정
1. 위와 동일한 방법으로 `develop` 브랜치에 대한 규칙 추가
2. **Branch name pattern**: `develop` 입력
3. 동일한 보호 옵션 적용
4. **Create** 클릭

**상세 가이드**: `docs/common/GITHUB_SETTINGS.md` 참조

---

### 3단계: Jira - GitHub 연동 (선택사항)

#### 3-1. Jira GitHub 앱 설치
1. GitHub Marketplace 방문: https://github.com/marketplace/jira-software-github
2. "Set up a plan" → "Install it for free" 클릭
3. 저장소 선택 (codwithyc/spring-sandbox)
4. "Install" 클릭

#### 3-2. Jira에서 GitHub 연결
1. Jira 관리 페이지 → Apps → Manage your apps
2. "GitHub for Jira" → "Get started"
3. GitHub 계정으로 인증
4. 저장소 연결

**상세 가이드**: `docs/common/JIRA_INTEGRATION.md` 참조

---

### 4단계: 설정 검증

#### 테스트 시나리오 1: 브랜치 보호 확인
```bash
# main 브랜치에 직접 푸시 시도 (실패해야 함)
git checkout main
echo "test" >> test.txt
git add test.txt
git commit -m "test: direct push to main"
git push origin main
# 예상: remote rejected (protected branch)
```

#### 테스트 시나리오 2: Feature 브랜치 워크플로우
```bash
# 1. develop에서 feature 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/PROJ-999-test-workflow

# 2. 테스트 파일 생성
echo "# Test" >> test-workflow.md
git add test-workflow.md
git commit -m "docs: PROJ-999 test Git Flow workflow"
git push -u origin feature/PROJ-999-test-workflow

# 3. GitHub에서 PR 생성
#    - Base: develop
#    - Compare: feature/PROJ-999-test-workflow
#    - Jira 티켓: PROJ-999

# 4. CI 자동 실행 확인
# 5. PR 머지 후 브랜치 삭제
```

#### 테스트 시나리오 3: Jira 연동 확인 (Jira 연동 완료 시)
1. Jira에서 새 티켓 생성 (예: PROJ-1000)
2. feature/PROJ-1000-jira-test 브랜치 생성
3. 커밋 메시지에 PROJ-1000 포함
4. Jira 티켓에서 Development 섹션 확인
5. 브랜치 및 커밋 정보 표시 확인

---

## 📋 최종 체크리스트

### GitHub 설정
- [ ] `develop` 브랜치 생성 및 푸시
- [ ] 기본 브랜치를 `develop`으로 변경
- [ ] `main` 브랜치 보호 규칙 설정
- [ ] `develop` 브랜치 보호 규칙 설정
- [ ] 브랜치 보호 테스트 완료

### Jira 연동 (선택사항)
- [ ] Jira GitHub 앱 설치
- [ ] Jira에서 GitHub 저장소 연결
- [ ] Development Panel 활성화 확인
- [ ] 테스트 티켓으로 연동 확인

### 팀 공유
- [ ] Git Flow 가이드 팀원과 공유
- [ ] 커밋 메시지 컨벤션 합의
- [ ] PR 프로세스 교육

---

## 📚 참고 문서

### 설정 가이드
- [Git Flow 초기 설정](./GIT_FLOW_SETUP.md)
- [GitHub 저장소 설정 (관리자용)](./GITHUB_SETTINGS.md)
- [Jira 연동 설정](./JIRA_INTEGRATION.md)

### 개발자 가이드
- [브랜치 전략](./BRANCH_STRATEGY.md)
- [커밋 메시지 컨벤션](./COMMIT_CONVENTION.md)
- [문서 인덱스](./README.md)

### 예시
- [Git Flow 테스트 예시](../examples/GIT_FLOW_TEST_EXAMPLE.md)

---

## 🆘 문제 해결

### 문제: develop 브랜치 푸시 권한 없음
**해결**: 저장소 관리자에게 권한 요청

### 문제: Status checks를 찾을 수 없음
**해결**: CI를 한 번 실행한 후 브랜치 보호 규칙에서 선택 가능

### 문제: Jira 연동이 작동하지 않음
**해결**: 
1. GitHub 앱 권한 확인
2. Jira Development Panel 활성화 확인
3. 티켓 번호 형식 확인 (프로젝트키-번호)

---

## 💡 추가 권장 사항

### 1. Commit Message Linting (선택사항)
커밋 메시지 형식을 자동으로 검증하려면:
```bash
npm install --save-dev @commitlint/cli @commitlint/config-conventional husky
npx husky install
```

### 2. Pre-commit Hooks (선택사항)
코드 품질 검사를 자동화하려면:
```bash
npm install --save-dev husky lint-staged
```

### 3. 릴리즈 자동화 (향후)
semantic-release 도구로 버전 관리 자동화 고려

---

## 📞 지원

질문이나 문제가 있으면:
1. GitHub Issues에 이슈 생성
2. 관련 문서 확인
3. 팀 채널에서 논의

---

*작성일: 2026-01-28*
*최종 업데이트: 2026-01-28*
