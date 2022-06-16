package com.aupma.spring.starter.rest;

import com.aupma.spring.starter.model.SampleDTO;
import com.aupma.spring.starter.service.SampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/sample")
@RequiredArgsConstructor
public class SampleResource {

    private final SampleService sampleService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SampleDTO>> getSamplesByName(@RequestParam final String name) {
        return ResponseEntity.ok(sampleService.findByName(name));
    }
}
