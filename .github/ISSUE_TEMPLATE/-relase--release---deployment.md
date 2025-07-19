---
name: "[Relase] Release / Deployment"
about: 새 버전 릴리즈나 배포 체크리스트 관리
title: ''
labels: "\U0001F30F Deploy"
assignees: codwithyc

---

---
name: "🚀 Release / Deployment"
about: "새 버전 릴리즈 또는 배포 시 체크리스트용 템플릿입니다."
title: "[RELEASE] v"
labels: ["release", "deployment"]
assignees: []
---

## 📦 릴리즈 버전
> vX.Y.Z

## 📝 주요 변경사항
-  

## 🔧 배포 전 체크리스트
- [ ] CI 파이프라인 성공  
- [ ] 스테이징에서 기능 검증  
- [ ] DB 마이그레이션 확인 (Flyway/Liquibase)  
- [ ] 환경변수/시크릿 설정  

## 🚀 배포 절차
1. `git tag vX.Y.Z && git push --tags`  
2. 자동 배포 확인  
3. Health check / Smoke test  

## 🔄 롤백 가이드
> 이전 태그로 롤백하는 방법 등

## 📌 참고 링크
- CI 로그:  
- 배포 문서: docs/DEPLOYMENT.md
