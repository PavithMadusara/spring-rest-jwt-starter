package com.aupma.spring.starter.rest;

import com.aupma.spring.starter.model.MfaRequestDTO;
import com.aupma.spring.starter.service.TotpService;
import com.aupma.spring.starter.service.UserService;
import com.aupma.spring.starter.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/verification", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VerificationResource {

    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private final UserService userService;

    @PostMapping("/totp")
    public ResponseEntity<Void> verifyTotp(@RequestBody MfaRequestDTO mfaRequest, @CurrentUser com.aupma.spring.starter.entity.User user) {
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
}
