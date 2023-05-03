package com.aupma.spring.starter.security.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class VerificationCodeDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String code;

    @NotNull
    private VerificationType type;

    @NotNull
    private OffsetDateTime expiresAt;

    private Long user;

}
