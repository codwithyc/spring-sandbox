package com.mycom.springsandbox.common;

import com.mycom.springsandbox.common.api.ApiEnvelopes;
import com.mycom.springsandbox.common.config.TimeConfig;
import com.mycom.springsandbox.common.handler.GlobalExceptionHandler;
import com.mycom.springsandbox.common.web.RequestIdFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestExceptionController.class)
@Import({GlobalExceptionHandler.class, ApiEnvelopes.class, TimeConfig.class, RequestIdFilter.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("비즈니스 예외는 공통 에러 응답과 requestId를 반환한다")
    void handleBusinessException() throws Exception {
        mockMvc.perform(get("/test/business").header("X-Request-Id", "req-123"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Request-Id", "req-123"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COM-4006"))
                .andExpect(jsonPath("$.error.requestId").value("req-123"))
                .andExpect(jsonPath("$.meta.requestId").value("req-123"));
    }

    @Test
    @DisplayName("검증 실패는 override된 MethodArgumentNotValid 핸들러를 탄다")
    void handleMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/test/echo")
                        .header("X-Request-Id", "req-456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COM-4003"))
                .andExpect(jsonPath("$.error.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.error.requestId").value("req-456"))
                .andExpect(jsonPath("$.meta.requestId").value("req-456"));
    }

    @Test
    @DisplayName("허용되지 않은 HTTP 메서드는 override된 핸들러를 탄다")
    void handleMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/test/echo").header("X-Request-Id", "req-789"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error.code").value("COM-4007"))
                .andExpect(jsonPath("$.error.requestId").value("req-789"))
                .andExpect(jsonPath("$.meta.requestId").value("req-789"));
    }
}
