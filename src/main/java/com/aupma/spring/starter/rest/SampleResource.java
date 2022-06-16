package com.aupma.spring.starter.rest;

import com.aupma.spring.starter.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/sample")
@RequiredArgsConstructor
public class SampleResource {

    private final SampleService sampleService;

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        return sampleService.hello();
    }
}
