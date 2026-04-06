package com.mycom.springsandbox.common.api;

import java.time.Instant;

public record ApiMeta(
    String requestId, // 요청-응답-로그를 연결하는 추적 ID : "req-123"
    Instant timestamp // 응답 생성 시각(UTC 기준) "2026-03-19T12:34:56.789Z"
) {
}
