package com.aupma.spring.starter.rest;

import com.aupma.spring.starter.entity.User;
import com.aupma.spring.starter.model.MfaRequestDTO;
import com.aupma.spring.starter.model.VerificationType;
import com.aupma.spring.starter.service.TotpService;
import com.aupma.spring.starter.service.UserService;
import com.aupma.spring.starter.service.VerificationService;
import com.aupma.spring.starter.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
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
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
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
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/get-code", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sendCode(@CurrentUser User user, @RequestParam VerificationType type) {
        switch (type) {
            case TOTP:
                return ResponseEntity.ok(totpService.getUriForImage(user.getMfaSecret()));
            case PHONE:
                if (user.getPhone() != null) {
                    verificationService.sendOTPCode(user.getPhone(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
                }
            case EMAIL:
                if (user.getEmail() != null) {
                    verificationService.sendEmailCode(user.getEmail(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
                }
        }
        return ResponseEntity.ok().build();
    }

}
