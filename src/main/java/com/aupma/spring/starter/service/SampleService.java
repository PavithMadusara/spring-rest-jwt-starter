package com.aupma.spring.starter.service;

import com.aupma.spring.starter.entity.Sample;
import com.aupma.spring.starter.model.SampleDTO;
import com.aupma.spring.starter.repos.SampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SampleService {

    private final SampleRepository sampleRepository;

    public List<SampleDTO> findByName(String name) {
        return sampleRepository.findByName(name).stream()
                .map(sample -> mapToDTO(sample, new SampleDTO()))
                .toList();
    }

    public SampleDTO mapToDTO(Sample sample, SampleDTO sampleDTO) {
        sampleDTO.setName(sample.getName());
        return sampleDTO;
    }
}
