package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.entity.Role;
import com.aupma.spring.starter.security.exception.ApplicationSecurityException;
import com.aupma.spring.starter.security.model.*;
import com.aupma.spring.starter.security.service.JwtTokenService;
import com.aupma.spring.starter.security.service.TotpService;
import com.aupma.spring.starter.security.service.UserService;
import com.aupma.spring.starter.security.service.VerificationService;
import com.aupma.spring.starter.security.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthResource {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final UserService userService;
    private final TotpService totpService;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = (User) authenticate.getPrincipal();

            AuthResponseDTO authResponse = new AuthResponseDTO();

            boolean isMfaEnabled = userService.getIsMfaEnabled(user.getUsername());

            if (isMfaEnabled) {
                authResponse.setIsMfaRequired(true);
                // Generating access token without any roles, this can be only used for MFA verification request
                authResponse.setAccessToken(tokenService.generateAccessToken(user, new ArrayList<>()));
                authResponse.setExpiresIn(tokenService.getExpiredDateFromToken(authResponse.getAccessToken()));
                return ResponseEntity.ok().body(authResponse);
            } else {
                Set<Role> roles = userService.getRoles(user.getUsername());
                return ResponseEntity.ok().body(generateAuthResponse(user, roles));
            }

        } catch (BadCredentialsException e) {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Invalid username or password");
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String username) {
        com.aupma.spring.starter.security.entity.User user = userService.getUser(username);
        if (user == null) {
            throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");
        }
        verificationService.sendPasswordResetLink(user.getId());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        com.aupma.spring.starter.security.entity.User user = userService.getUser(resetPasswordDTO.getUsername());
        if (user == null) {
            throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");
        }
        Boolean verified = verificationService.verifyResetToken(user.getId(), resetPasswordDTO.getToken());
        if (!verified) {
            throw new ApplicationSecurityException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid token");
        }
        userService.updatePassword(user.getId(), resetPasswordDTO.getPassword());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO, @CurrentUser com.aupma.spring.starter.security.entity.User user) {
        boolean matches = passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword());
        if (!matches) {
            throw new ApplicationSecurityException(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "Invalid password");
        }
        userService.updatePassword(user.getId(), updatePasswordDTO.getNewPassword());
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "/get-code", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sendCode(
            @CurrentUser com.aupma.spring.starter.security.entity.User user,
            @RequestParam VerificationType type
    ) {
        switch (type) {
            case TOTP:
                return ResponseEntity.ok(totpService.getUriForImage(user.getMfaSecret()));
            case PHONE:
                if (user.getPhone() != null) {
                    verificationService.sendOTPCode(user.getPhone(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "PHONE_NOT_AVAILABLE", "Phone not available");
                }
            case EMAIL:
                if (user.getEmail() != null) {
                    verificationService.sendEmailCode(user.getEmail(), user.getId());
                    return ResponseEntity.ok().build();
                } else {
                    throw new ApplicationSecurityException(HttpStatus.NOT_FOUND, "EMAIL_NOT_AVAILABLE", "Email not available");
                }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<AuthResponseDTO> verifyTotp(
            Authentication authentication,
            @RequestBody MfaRequestDTO mfaRequest,
            @CurrentUser com.aupma.spring.starter.security.entity.User user
    ) {
        User userDetails = (User) authentication.getPrincipal();
        boolean verified = totpService.verifyCode(mfaRequest.getCode(), user.getMfaSecret());
        if (verified) {
            Set<Role> roles = userService.getRoles(userDetails.getUsername());
            return ResponseEntity.ok().body(generateAuthResponse(userDetails, roles));
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_TOTP_CODE", "Invalid code");
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponseDTO> verifyEmail(
            Authentication authentication,
            @RequestBody MfaRequestDTO mfaRequest,
            @CurrentUser com.aupma.spring.starter.security.entity.User user
    ) {
        User userDetails = (User) authentication.getPrincipal();
        Boolean verifyEmail = verificationService.verifyEmail(mfaRequest.getCode(), user.getId());
        if (verifyEmail) {
            Set<Role> roles = userService.getRoles(userDetails.getUsername());
            return ResponseEntity.ok().body(generateAuthResponse(userDetails, roles));
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_EMAIL_CODE", "Invalid code");
        }
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<AuthResponseDTO> verifyPhone(
            Authentication authentication,
            @RequestBody MfaRequestDTO mfaRequest,
            @CurrentUser com.aupma.spring.starter.security.entity.User user
    ) {
        User userDetails = (User) authentication.getPrincipal();
        Boolean verifyPhone = verificationService.verifyPhone(mfaRequest.getCode(), user.getId());
        if (verifyPhone) {
            Set<Role> roles = userService.getRoles(userDetails.getUsername());
            return ResponseEntity.ok().body(generateAuthResponse(userDetails, roles));
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_PHONE_CODE", "Invalid code");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser com.aupma.spring.starter.security.entity.User user) {
        return ResponseEntity.ok().body(userService.mapToDTO(user, new UserDTO()));
    }

    @PostMapping("/token-refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody TokenRequestDTO requestDTO) {
        UserDetails userDetails = userService.loadUserByUsername(
                tokenService.getUserNameFromToken(requestDTO.getRefreshToken())
        );
        Set<Role> roles = userService.getRoles(userDetails.getUsername());
        boolean isValidToken = tokenService.validateToken(requestDTO.getRefreshToken(), userDetails);
        if (isValidToken) {
            return ResponseEntity.ok().body(generateAuthResponse(userDetails, roles));
        } else {
            throw new ApplicationSecurityException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token");
        }
    }

    private AuthResponseDTO generateAuthResponse(UserDetails userDetails, Set<Role> roles) {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setAccessToken(tokenService.generateAccessToken(userDetails, roles.stream().toList()));
        authResponse.setRefreshToken(tokenService.generateRefreshToken(userDetails));
        authResponse.setExpiresIn(tokenService.getExpiredDateFromToken(authResponse.getAccessToken()));
        return authResponse;
    }
}
