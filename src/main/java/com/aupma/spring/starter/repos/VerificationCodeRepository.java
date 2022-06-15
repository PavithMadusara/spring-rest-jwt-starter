package com.aupma.spring.starter.repos;

import com.aupma.spring.starter.entity.VerificationCode;
import com.aupma.spring.starter.model.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByCodeAndUserAndType(String code, Long user, VerificationType type);
}
