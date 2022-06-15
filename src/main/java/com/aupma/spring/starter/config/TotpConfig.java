package com.aupma.spring.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration()
@ConfigurationProperties("totp")
public class TotpConfig {
    private String qrLabel;
    private String issuer;
}
