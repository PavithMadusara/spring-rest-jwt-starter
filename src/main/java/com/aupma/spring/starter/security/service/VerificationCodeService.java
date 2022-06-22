package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.entity.User;
import com.aupma.spring.starter.security.entity.VerificationCode;
import com.aupma.spring.starter.security.model.VerificationCodeDTO;
import com.aupma.spring.starter.security.model.VerificationType;
import com.aupma.spring.starter.security.repos.UserRepository;
import com.aupma.spring.starter.security.repos.VerificationCodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;

    public VerificationCodeService(final VerificationCodeRepository verificationCodeRepository,
                                   final UserRepository userRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }


    public VerificationCodeDTO get(final Long userId, final VerificationType type, final String code) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        VerificationCode verificationCode = verificationCodeRepository.findByCodeAndUserAndType(code, user, type);
        if (verificationCode == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code not found");
        } else {
            if (verificationCode.getExpiresAt().isBefore(OffsetDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code expired");
            } else {
                return mapToDTO(verificationCode, new VerificationCodeDTO());
            }
        }
    }

    public Long create(final VerificationCodeDTO verificationCodeDTO) {
        final VerificationCode verificationCode = new VerificationCode();
        mapToEntity(verificationCodeDTO, verificationCode);
        return verificationCodeRepository.save(verificationCode).getId();
    }

    public void delete(final Long id) {
        verificationCodeRepository.deleteById(id);
    }

    public VerificationCodeDTO mapToDTO(final VerificationCode verificationCode,
                                        final VerificationCodeDTO verificationCodeDTO) {
        verificationCodeDTO.setId(verificationCode.getId());
        verificationCodeDTO.setCode(verificationCode.getCode());
        verificationCodeDTO.setType(verificationCode.getType());
        verificationCodeDTO.setExpiresAt(verificationCode.getExpiresAt());
        verificationCodeDTO.setUser(verificationCode.getUser() == null ? null : verificationCode.getUser().getId());
        return verificationCodeDTO;
    }

    public void mapToEntity(final VerificationCodeDTO verificationCodeDTO,
                            final VerificationCode verificationCode) {
        verificationCode.setCode(verificationCodeDTO.getCode());
        verificationCode.setType(verificationCodeDTO.getType());
        verificationCode.setExpiresAt(verificationCodeDTO.getExpiresAt());
        final User user = verificationCodeDTO.getUser() == null ? null : userRepository.findById(verificationCodeDTO.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        verificationCode.setUser(user);
    }

}
