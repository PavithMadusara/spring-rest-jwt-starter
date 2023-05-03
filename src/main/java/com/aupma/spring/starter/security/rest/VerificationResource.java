package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.entity.User;
import com.aupma.spring.starter.security.exception.ApplicationSecurityException;
import com.aupma.spring.starter.security.model.MfaRequestDTO;
import com.aupma.spring.starter.security.model.VerificationType;
import com.aupma.spring.starter.security.service.TotpService;
import com.aupma.spring.starter.security.service.UserService;
import com.aupma.spring.starter.security.service.VerificationService;
import com.aupma.spring.starter.security.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/verification", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VerificationResource {

    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private final UserService userService;
    private final VerificationService verificationService;

    /**
     * Verifies TOTP code and enables MFA and TOTP method for user
     *
     * @param mfaRequest DTO with TOTP code and user's password
     * @param user       User object
     * @return Status code 200 if verification was successful, 401 if password is incorrect, 403 if totp code is incorrect
     */
    @PostMapping("/totp")
    public ResponseEntity<Void> verifyTotp(@RequestBody MfaRequestDTO mfaRequest, @CurrentUser User user) {
        boolean matches = passwordEncoder.matches(mfaRequest.getPassword(), user.getPassword());
        if (matches) {
            boolean verified = totpService.verifyCode(mfaRequest.getCode(), user.getMfaSecret());
            if (verified) {
                userService.enableTotp(user.getUsername());
                return ResponseEntity.ok().build();
            } else {
                throw new ApplicationSecurityException(HttpStatus.FORBIDDEN, "INVALID_TOTP_CODE", "Invalid TOTP code");
            }
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "Invalid password");
        }
    }

    @PostMapping("/email")
    public ResponseEntity<Void> verifyEmail(@RequestBody MfaRequestDTO mfaRequest, @CurrentUser User user) {
        boolean matches = passwordEncoder.matches(mfaRequest.getPassword(), user.getPassword());
        if (matches) {
            Boolean verifyEmail = verificationService.verifyEmail(mfaRequest.getCode(), user.getId());
            if (verifyEmail) {
                return ResponseEntity.ok().build();
            } else {
                throw new ApplicationSecurityException(HttpStatus.FORBIDDEN, "INVALID_EMAIL_CODE", "Invalid email code");
            }
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "Invalid password");
        }
    }

    @PostMapping("/phone")
    public ResponseEntity<Void> verifyPhone(@RequestBody MfaRequestDTO mfaRequest, @CurrentUser User user) {
        boolean matches = passwordEncoder.matches(mfaRequest.getPassword(), user.getPassword());
        if (matches) {
            Boolean verifyPhone = verificationService.verifyPhone(mfaRequest.getCode(), user.getId());
            if (verifyPhone) {
                return ResponseEntity.ok().build();
            } else {
                throw new ApplicationSecurityException(HttpStatus.FORBIDDEN, "INVALID_PHONE_CODE", "Invalid phone code");
            }
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "Invalid password");
        }
    }

    @PostMapping(value = "/get-qr-code", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTOTPQrCode(@CurrentUser User user) {
        return ResponseEntity.ok(totpService.getUriForImage(user.getMfaSecret()));
    }

    @PostMapping(value = "/get-code")
    public ResponseEntity<String> sendCode(@CurrentUser User user, @RequestParam VerificationType type) {
        switch (type) {
            case PHONE:
                if (user.getPhone() != null) {
                    verificationService.sendOTPCode(user.getPhone(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "PHONE_NUMBER_NOT_FOUND", "Phone number is not available");
                }
            case EMAIL:
                if (user.getEmail() != null) {
                    verificationService.sendEmailCode(user.getEmail(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND", "Email is not available");
                }
        }
        return ResponseEntity.ok().build();
    }

}
