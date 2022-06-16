package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.model.VerificationCodeDTO;
import com.aupma.spring.starter.security.model.VerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationCodeService verificationCodeService;

    public void sendOTPCode(String phone, Long userId) {
        SecureRandom random = new SecureRandom();
        String randomCode = new BigInteger(30, random).toString(32).toUpperCase();
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setCode(randomCode);
        verificationCodeDTO.setType(VerificationType.PHONE);
        verificationCodeDTO.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
        verificationCodeDTO.setUser(userId);
        verificationCodeService.create(verificationCodeDTO);

        System.out.println("Sending OTP code: " + randomCode + " to phone: " + phone);
    }

    public void sendEmailCode(String email, Long userId) {
        SecureRandom random = new SecureRandom();
        String randomCode = new BigInteger(30, random).toString(32).toUpperCase();
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setCode(randomCode);
        verificationCodeDTO.setType(VerificationType.EMAIL);
        verificationCodeDTO.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
        verificationCodeDTO.setUser(userId);
        verificationCodeService.create(verificationCodeDTO);

        System.out.println("Sending Verification code: " + randomCode + " to email: " + email);
    }

    public Boolean verifyEmail(String code, Long userId) {
        VerificationCodeDTO codeDTO = verificationCodeService.get(userId, VerificationType.EMAIL, code);
        verificationCodeService.delete(codeDTO.getId());
        return true;
    }

    public Boolean verifyPhone(String code, Long id) {
        VerificationCodeDTO codeDTO = verificationCodeService.get(id, VerificationType.PHONE, code);
        verificationCodeService.delete(codeDTO.getId());
        return true;
    }

    public void sendPasswordResetLink(Long userId) {

        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setCode(UUID.randomUUID().toString());
        verificationCodeDTO.setType(VerificationType.PASSWORD_RESET);
        verificationCodeDTO.setExpiresAt(OffsetDateTime.now().plusMinutes(5));
        verificationCodeDTO.setUser(userId);
        verificationCodeService.create(verificationCodeDTO);

        System.out.println("Sending password reset link: " + verificationCodeDTO.getCode() + " to user: " + userId);
    }

    public Boolean verifyResetToken(Long id, String token) {
        VerificationCodeDTO codeDTO = verificationCodeService.get(id, VerificationType.PASSWORD_RESET, token);
        verificationCodeService.delete(codeDTO.getId());
        return true;
    }
}
