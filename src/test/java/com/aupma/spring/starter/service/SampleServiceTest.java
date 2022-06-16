package com.aupma.spring.starter.service;

import com.aupma.spring.starter.entity.Sample;
import com.aupma.spring.starter.model.SampleDTO;
import com.aupma.spring.starter.repos.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;
    private SampleService underTest;

    @BeforeEach
    void setUp() {
        underTest = new SampleService(sampleRepository);
    }

    @Test
    void findByName() {
        // when
        underTest.findByName("test-sample");
        //then
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(sampleRepository).findByName(argumentCaptor.capture());

        String value = argumentCaptor.getValue();
        assertEquals("test-sample", value);
    }

    @Test
    void mapToDTO() {
        // given
        Sample sample = new Sample();
        sample.setName("test-sample");
        // when
        SampleDTO sampleDTO = underTest.mapToDTO(sample, new SampleDTO());
        // then
        assertEquals("test-sample", sampleDTO.getName());
    }
}
