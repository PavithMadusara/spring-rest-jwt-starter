package com.aupma.spring.starter.repos;

import com.aupma.spring.starter.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleRepository extends JpaRepository<Sample, Long> {
    List<Sample> findByName(String name);
}
