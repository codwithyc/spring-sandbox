# Branching Strategy (Gitflow)
## 1. 기본 브랜치
| 브랜치 | 역할 |
|------|------|
| main | 프로덕션 브랜치. 태그 기반 릴리즈 및 운영 배포 기준 |
| dev | 개발 통합 브랜치. 모든 기능 브랜치의 머지 대상 |

## 2. 브랜치 타입 (Prefix)
모든 작업 브랜치는 Github Issude Number를 기준으로 합니다.

형식:
```
<prefix>/<issueNumber>-<short-description>
```

예:
```
feature/123-login-api
refactor/245-auth-service-cleanup
chore/10-update-gradle
```

### Prefix 종류
| Prefix | 용도 |
|------|------|
| feature | 신규 기능 개발 |
| refactor | 리팩토링 (기능 변경 없음) |
| fix | 일반 버그 수정 (당일 배포 필요 없음) |
| chore | 빌드, 설정, 의존성, 스크립트, 문서 변경 |
| test | 테스트 코드 추가/수정 |
| docs | 문서 수정 |

## 3. Feature / Refactor / Fix / Chore 흐름

1. dev 브랜치에서 작업 브랜치 생성
```
git checkout dev
git checkout -b feature/123-login-api
```
2. 개발 및 커밋
3. dev 대상으로 Pull Request 생성
4. CI(빌드/테스트) 통과 + 코드 리뷰 승인
5. dev 브랜치로 머지

## 4. Release 브랜치

릴리즈 단위 묶음을 위한 브랜치

형식:
```
release/vX.Y.Z
```

생성:
```
git checkout dev
git checkout -b release/v1.2.0
```

규칙:
- QA 및 최종 테스트만 수행
- 새로운 기능 추가 금지
- 버그 수정만 허용

완료 절차:
1. release 브랜치를 main으로 머지
2. main에 태그 생성
```
git tag v1.2.0
git push origin v1.2.0
```
3. release 브랜치를 dev에도 머지

## 5. Hotfix 브랜치 (당일 배포 기준)

형식:
```
hotfix/<issueNumber>-<short-description>
```

사용 기준:
- 프로덕션 장애
- 보안 이슈
- 데이터 정합성 오류
- 당일 내 배포가 반드시 필요한 수정

흐름:
1. main 브랜치에서 hotfix 브랜치 생성
```
git checkout main
git checkout -b hotfix/999-payment-null-pointer
```
2. 수정 및 커밋
3. main 브랜치로 머지
4. 태그 생성 및 푸시 (즉시 운영 배포)
```
git tag v1.2.1
git push origin v1.2.1
```
5. 동일 변경 사항을 dev 브랜치에도 머지

## 6. Pull Request 규칙

- 모든 PR은 Github Issue 포함
- PR 제목 형식:
```
[ANS-123] Login API 구현
```
- 최소 1명 이상 리뷰 승인 후 머지 - 코파일럿의 승인을 포함
- CI 실패 시 머지 금지

## 7. 태그 규칙

형식:
```
v<major>.<minor>.<patch>
```

예:
```
v1.0.0
v1.2.3
```
- main 브랜치에서만 태그 생성
- 태그 푸시 시 운영 배포 트리거

## 8. 머지 전략
- dev ← feature/refactor/fix/chore : Squash Merge
- main ← release/hotfix : Merge Commit

## 9. 금지 사항
- main 브랜치 직접 커밋 금지
- dev 브랜치 직접 커밋 금지