package com.mycom.springsandbox.common;

import com.mycom.springsandbox.common.exception.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
class TestExceptionController {

    @GetMapping("/business")
    String business() {
        throw new NotFoundException("Member", 1L);
    }

    @PostMapping("/echo")
    EchoRequest echo(@Valid @RequestBody EchoRequest request) {
        return request;
    }

    record EchoRequest(@NotBlank String name) {
    }
}
