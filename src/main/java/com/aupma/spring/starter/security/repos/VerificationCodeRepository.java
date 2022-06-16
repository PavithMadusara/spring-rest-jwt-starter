package com.aupma.spring.starter.security.repos;

import com.aupma.spring.starter.security.entity.VerificationCode;
import com.aupma.spring.starter.security.model.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByCodeAndUserAndType(String code, Long user, VerificationType type);
}
