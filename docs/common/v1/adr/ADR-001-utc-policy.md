# ADR-001-utc-policy

## 1. 배경 (Context)
공통 응답 구조에서 `ApiMeta.timestamp`는 사용자 화면 표시용 시간이 아니라,  
응답 생성 시점과 요청-응답-로그를 추적하기 위한 운영 메타데이터라고 판단했습니다.

현재 서비스는 주 사용자가 한국에 있지만, 시스템 내부 시간 기준을 KST로 고정하면  
향후 해외 사용자 또는 외부 시스템 연동 시 시간 해석과 비교가 복잡해질 수 있다고 보았습니다.

시간 표현 방식으로는 `LocalDateTime`과 `Instant`를 검토했습니다.

## 2. 결정 (Decision)
`ApiMeta.timestamp`는 UTC 기준 `Instant`를 사용하기로 결정했습니다.

서버 내부에서 기록되는 운영 메타데이터 시간은 UTC로 통일하고,  
사용자 화면이나 운영 UI에서 필요한 경우에만 로컬 시간대로 변환해 표시하기로 했습니다.

## 3. 대안 (Alternatives)

### 3.1 `LocalDateTime`
- 사람이 읽기에는 직관적이라고 보았습니다.
- 시간대 정보가 없어 절대 시점을 명확하게 표현하기 어렵다고 판단했습니다.
- 시스템 간 로그, DB, 외부 API 시각을 비교할 때 해석 차이가 생길 수 있다고 보았습니다.

### 3.2 `Instant`
- UTC 기준 절대 시점을 표현할 수 있다고 판단했습니다.
- 로그, 예외 시점, 외부 시스템 기록과 비교하기 쉽다고 보았습니다.
- 운영 추적용 메타데이터에 더 적합하다고 판단했습니다.

## 4. 결과 (Consequences)
- 요청-응답-로그 간 시점 비교 기준을 일관되게 유지할 수 있게 되었습니다.
- 향후 다국가 환경으로 확장되더라도 내부 시간 기준이 흔들리지 않도록 했습니다.
- 사용자에게 보여주는 시간은 별도 시간대 변환이 필요하게 되었습니다.
- 비즈니스 로컬 시간은 이 정책과 분리해서 다루기로 했습니다.

## 6. 관련 문서/코드 링크
- `src/main/java/com/mycom/springsandbox/common/api/ApiMeta.java`
- `src/main/java/com/mycom/springsandbox/common/api/ApiEnvelopes.java`
- `src/main/java/com/mycom/springsandbox/common/config/TimeConfig.java`
