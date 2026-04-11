package com.mycom.springsandbox.common.api;

import com.mycom.springsandbox.common.error.ApiError;
import com.mycom.springsandbox.common.web.RequestIds;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;

@Component
public class ApiEnvelopes {

    private final Clock clock;

    public ApiEnvelopes(Clock clock) {
        this.clock = clock;
    }

    public <T> ResponseEntity<ApiEnvelope<T>> ok(T data, HttpServletRequest request) {
        return ResponseEntity.ok(ApiEnvelope.ok(data, meta(request)));
    }

    public <T> ResponseEntity<ApiEnvelope<T>> created(URI location, T data, HttpServletRequest request) {
        return ResponseEntity.created(location).body(ApiEnvelope.ok(data, meta(request)));
    }

    public ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<ApiEnvelope<Void>> fail(HttpStatus status, ApiError error, HttpServletRequest request) {
        return ResponseEntity.status(status).body(ApiEnvelope.fail(error, meta(request)));
    }

    private ApiMeta meta(HttpServletRequest request) {
        String requestId = (String) request.getAttribute(RequestIds.REQUEST_ID_ATTR);
        return new ApiMeta(requestId, Instant.now(clock));
    }
}

