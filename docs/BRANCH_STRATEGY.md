# 브랜치 전략 (Branching Strategy)

## 🎯 목적
- 사이드 프로젝트에 현업 수준의 Gitflow 워크플로우를 적용하여 버전 관리 및 배포·롤백 절차를 명확히 함
- 팀 협업 및 이직 포트폴리오에서 어필할 수 있는 체계적인 개발 프로세스 구축

## 📂 브랜치 종류 및 역할
| 브랜치명                         | 역할 및 설명                                                                                     |
|---------------------------------|--------------------------------------------------------------------------------------------------|
| `main`                          | 프로덕션 배포용. 태그를 통한 릴리즈 관리 및 프로덕션 자동 배포 트리거                             |
| `develop`                       | 스테이징 배포용. feature 브랜치 머지 후 자동 스테이징 배포                                        |
| `feature/{이슈번호}-{키워드}`    | 신규 기능 개발용. 개발 완료 시 `develop`으로 Pull Request                                        |
| `release/vX.Y.Z`                | 릴리즈 준비 및 QA용. QA 완료 후 `main`에 머지 및 `vX.Y.Z` 태그 생성                             |
| `hotfix/{이슈번호}-{키워드}`     | 긴급 수정용. `main`에서 직접 브랜치 생성 → 수정 후 `main` 머지(배포) → `develop` 머지            |

## 🔄 워크플로우
1. **기능 개발 (Feature)**
    - `feature/{이슈번호}-{키워드}` 브랜치 생성
    - 기능 구현 → 커밋
    - PR 생성 → 자동 CI(빌드·테스트) 통과 후 코드 리뷰 및 승인
    - `develop`에 머지 → 스테이징 자동 배포

2. **릴리즈 준비 (Release)**
    - `develop` 브랜치에서 `release/vX.Y.Z` 브랜치 생성
    - QA 및 최종 테스트 수행, 버그 수정 커밋
    - QA 완료 시 `main` 브랜치로 머지
    - `vX.Y.Z` 태그 생성 및 푸시 → 프로덕션 자동 배포

3. **핫픽스 (Hotfix)**
    - 프로덕션에서 긴급 수정 사항 발생 시 `hotfix/{이슈번호}-{키워드}` 브랜치 생성
    - 수정 → 커밋 → `main`에 머지 → 프로덕션 배포
    - `develop` 브랜치에도 동일 수정 머지

## 🔖 네이밍 컨벤션
- 브랜치명, 태그: 모두 소문자 + 케밥 케이스 사용 (예: `feature/123-login-api`, `release/v1.0.0`)
- 이슈번호 포함: 연관된 이슈와 연계하여 추적 용이
- 태그: `v{메이저}.{마이너}.{패치}` 형식 준수

## 🛠 세부 가이드 및 참고
- **CI 설정**: `.github/workflows/ci.yml`, `cd.yml` 확인
- **DB 마이그레이션**: Flyway 또는 Liquibase 스크립트(`src/main/resources/db/migration/`) 버전 관리
- **배포 문서**: `docs/DEPLOYMENT.md`에서 상세 배포 절차 참고
- **이슈 템플릿**: `.github/ISSUE_TEMPLATE/` 내 Docs, Bug, Feature, Release 등 템플릿 활용

---

*이 문서는 Git 커밋 이력을 통해 업데이트되며, 정책 변경 시 이슈를 통해 논의 후 수정해주세요.*
