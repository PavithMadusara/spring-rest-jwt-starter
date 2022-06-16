package com.aupma.spring.starter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleService {
    public String hello() {
        return "Hello World!";
    }
}
