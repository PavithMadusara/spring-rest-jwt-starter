package com.aupma.spring.starter.repos;

import com.aupma.spring.starter.entity.Sample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SampleRepositoryTest {

    @Autowired
    private SampleRepository underTest;

    @Test
    void findByName() {
        // given
        String name = "test-sample";

        Sample sample = new Sample();
        sample.setName(name);
        underTest.save(sample);

        // when
        List<Sample> samples = underTest.findByName(name);

        // then
        assertEquals(1, samples.size());
    }
}
