package com.aupma.spring.starter.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
