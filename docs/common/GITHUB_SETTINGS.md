# GitHub 저장소 설정 가이드 (관리자용)

## 📌 개요
이 문서는 Git Flow와 Jira 연동을 위한 GitHub 저장소 설정 방법을 설명합니다.
**주의**: 이 작업은 저장소 관리자 권한이 필요합니다.

## 🔧 필수 설정 작업

### 1. 기본 브랜치 변경

#### 목적
- `main` 브랜치를 프로덕션 전용으로 사용
- `develop` 브랜치를 기본 개발 브랜치로 사용
- 새로운 PR이 기본적으로 `develop` 브랜치를 타겟으로 설정

#### 설정 방법
1. GitHub 저장소 페이지 방문
2. **Settings** 탭 클릭
3. 왼쪽 메뉴에서 **Branches** 클릭
4. **Default branch** 섹션에서 현재 기본 브랜치 확인 (main)
5. 브랜치 전환 아이콘 (⇄) 클릭
6. 드롭다운에서 **develop** 선택
7. **Update** 버튼 클릭
8. 확인 다이얼로그에서 **I understand, update the default branch.** 클릭

#### 확인
- 저장소 메인 페이지에서 기본 브랜치가 `develop`으로 표시되는지 확인
- 새 PR 생성 시 base 브랜치가 자동으로 `develop`으로 설정되는지 확인

---

### 2. main 브랜치 보호 규칙 설정

#### 목적
- 프로덕션 브랜치 보호
- 직접 푸시 방지
- 코드 리뷰 필수화
- CI 통과 필수화

#### 설정 방법
1. **Settings** → **Branches** 이동
2. **Branch protection rules** 섹션에서 **Add branch protection rule** 클릭
3. **Branch name pattern**에 `main` 입력

4. 다음 옵션 활성화:

   **Protect matching branches**
   - ✅ **Require a pull request before merging**
     - ✅ Require approvals: `1` (최소 1명의 승인 필요)
     - ✅ Dismiss stale pull request approvals when new commits are pushed
     - ✅ Require review from Code Owners (선택사항)
   
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - Status checks: `build` (CI 워크플로우 job 이름)
   
   - ✅ **Require conversation resolution before merging**
     - PR의 모든 코멘트가 해결되어야 머지 가능
   
   - ✅ **Require signed commits** (선택사항, 보안 강화)
   
   - ✅ **Require linear history** (선택사항, 깔끔한 히스토리)
   
   - ✅ **Do not allow bypassing the above settings**
     - 관리자도 규칙 우회 불가 (권장)

5. **Create** 버튼 클릭

#### 설정 결과
- `main` 브랜치에 직접 푸시 불가
- PR을 통해서만 변경 가능
- 최소 1명의 승인 필요
- CI가 통과해야 머지 가능
- 모든 코멘트 해결 필요

---

### 3. develop 브랜치 보호 규칙 설정

#### 목적
- 개발 브랜치 안정성 유지
- CI 자동 실행
- 코드 품질 관리

#### 설정 방법
1. **Settings** → **Branches** 이동
2. **Branch protection rules** 섹션에서 **Add branch protection rule** 클릭
3. **Branch name pattern**에 `develop` 입력

4. 다음 옵션 활성화:

   **Protect matching branches**
   - ✅ **Require a pull request before merging**
     - ✅ Require approvals: `1`
     - ✅ Dismiss stale pull request approvals when new commits are pushed
   
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - Status checks: `build`
   
   - ✅ **Require conversation resolution before merging**
   
   - ⬜ **Do not allow bypassing the above settings** (선택사항)
     - develop은 main보다 유연하게 관리 가능

5. **Create** 버튼 클릭

#### 설정 결과
- `develop` 브랜치에 직접 푸시 불가
- PR을 통해서만 변경 가능
- CI가 통과해야 머지 가능

---

### 4. 태그 보호 규칙 설정 (선택사항)

#### 목적
- 릴리즈 태그 보호
- 무단 태그 생성/삭제 방지

#### 설정 방법
1. **Settings** → **Tags** 이동
2. **Protected tags** 섹션에서 **Add tag protection rule** 클릭
3. **Tag name pattern**에 `v*` 입력 (v로 시작하는 모든 태그)
4. **Save** 클릭

#### 설정 결과
- `v*` 형식의 태그는 관리자만 생성/삭제 가능

---

### 5. GitHub Actions 권한 설정

#### 목적
- CI/CD 워크플로우 실행 권한 관리
- PR에서 워크플로우 실행 허용

#### 설정 방법
1. **Settings** → **Actions** → **General** 이동
2. **Actions permissions** 섹션에서 다음 선택:
   - ✅ Allow all actions and reusable workflows
   
3. **Workflow permissions** 섹션에서:
   - ✅ Read and write permissions (선택)
   - 또는 ✅ Read repository contents and packages permissions (권장)
   - ✅ Allow GitHub Actions to create and approve pull requests (필요시)

4. **Save** 클릭

---

## 📋 설정 체크리스트

### 초기 설정
- [ ] `develop` 브랜치가 존재하는지 확인
- [ ] 기본 브랜치를 `develop`으로 변경
- [ ] `main` 브랜치 보호 규칙 설정
- [ ] `develop` 브랜치 보호 규칙 설정
- [ ] 태그 보호 규칙 설정 (선택)
- [ ] GitHub Actions 권한 설정

### 설정 확인
- [ ] 새 PR 생성 시 base 브랜치가 `develop`인지 확인
- [ ] `main` 브랜치에 직접 푸시 시도 → 거부되는지 확인
- [ ] `develop` 브랜치에 직접 푸시 시도 → 거부되는지 확인
- [ ] feature 브랜치에서 `develop`으로 PR 생성 → CI 실행 확인

---

## 🔍 설정 후 테스트

### 테스트 시나리오

#### 1. 브랜치 보호 테스트
```bash
# main 브랜치에 직접 푸시 시도 (실패해야 함)
git checkout main
echo "test" >> test.txt
git add test.txt
git commit -m "test: direct push to main"
git push origin main
# 예상 결과: remote rejected (protected branch)
```

#### 2. PR 워크플로우 테스트
```bash
# 1. feature 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/test-branch-protection

# 2. 변경사항 커밋
echo "# Test" >> test.md
git add test.md
git commit -m "docs: test branch protection"
git push -u origin feature/test-branch-protection

# 3. GitHub에서 PR 생성
# - Base: develop
# - Compare: feature/test-branch-protection
# - CI 자동 실행 확인
# - 리뷰 요청
# - 승인 후 머지
```

#### 3. 태그 생성 테스트
```bash
# release 브랜치에서 태그 생성
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
# 관리자만 가능해야 함
```

---

## 🛠️ 트러블슈팅

### 문제: 브랜치 보호 규칙 저장 시 오류
**원인**: 
- 저장소 관리자 권한 없음
- 플랜 제한 (무료 플랜은 일부 기능 제한)

**해결방법**:
1. 저장소 소유자에게 관리자 권한 요청
2. GitHub Pro/Team 플랜 고려

### 문제: Status checks를 찾을 수 없음
**원인**: 
- CI 워크플로우가 아직 실행되지 않음
- Job 이름이 다름

**해결방법**:
1. CI 워크플로우를 한 번 실행
2. 정확한 job 이름 확인 (`.github/workflows/*.yml`)
3. 브랜치 보호 규칙에서 정확한 job 이름 입력

### 문제: 관리자도 푸시가 안 됨
**원인**: 
- "Do not allow bypassing the above settings" 활성화

**해결방법**:
1. 이것이 의도된 동작입니다
2. 관리자도 PR을 통해 변경해야 합니다 (권장)
3. 긴급한 경우 브랜치 보호 규칙 일시 비활성화

---

## 📚 참고 자료

- [GitHub Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub Actions Permissions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication)
- [Git Flow 설정 가이드](./GIT_FLOW_SETUP.md)

---

## 📸 설정 스크린샷 예시

### 1. Default Branch 설정
```
Settings → Branches → Default branch
현재: main
변경: develop → Update
```

### 2. Branch Protection Rule
```
Settings → Branches → Branch protection rules → Add rule
Branch name pattern: main

☑ Require a pull request before merging
  ☑ Require approvals: 1
  ☑ Dismiss stale pull request approvals when new commits are pushed

☑ Require status checks to pass before merging
  ☑ Require branches to be up to date before merging
  Status checks: build

☑ Require conversation resolution before merging
☑ Do not allow bypassing the above settings

→ Create
```

---

*최종 업데이트: 2026-01-28*
