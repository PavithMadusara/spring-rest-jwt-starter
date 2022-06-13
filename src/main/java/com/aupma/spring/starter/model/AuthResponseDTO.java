package com.aupma.spring.starter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Date expiresIn;
}
