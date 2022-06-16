package com.aupma.spring.starter.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequestDTO {
    private String refreshToken;
}
