# Git Flow 테스트 예시

## 목적
이 파일은 Git Flow 워크플로우와 Jira 연동을 테스트하기 위한 예시 파일입니다.

## 테스트 시나리오

### 1. Feature 브랜치 생성
```bash
git checkout develop
git pull origin develop
git checkout -b feature/PROJ-001-test-gitflow-workflow
```

### 2. 기능 개발
이 파일은 Git Flow 테스트를 위해 생성되었습니다.

### 3. 커밋 메시지 작성 (Jira 티켓 번호 포함)
```bash
git add .
git commit -m "feat(test): PROJ-001 add Git Flow test example"
```

### 4. 원격 저장소에 푸시
```bash
git push -u origin feature/PROJ-001-test-gitflow-workflow
```

### 5. Pull Request 생성
- Base: develop
- Compare: feature/PROJ-001-test-gitflow-workflow
- Title: `feat(test): PROJ-001 add Git Flow test example`
- Jira 티켓: PROJ-001

## 확인 사항
- [x] 브랜치 네이밍 규칙 준수
- [x] 커밋 메시지에 Jira 티켓 번호 포함
- [x] Conventional Commits 형식 준수
- [x] develop 브랜치 타겟

## 예상 결과
1. GitHub에서 PR 생성 완료
2. CI 워크플로우 자동 실행
3. Jira에서 브랜치 및 커밋 정보 확인 가능 (연동 완료 시)
4. PR 리뷰 및 승인 후 develop 브랜치로 머지

---
*생성일: 2026-01-28*
