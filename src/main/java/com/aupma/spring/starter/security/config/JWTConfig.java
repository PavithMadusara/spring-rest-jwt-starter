package com.aupma.spring.starter.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration()
@ConfigurationProperties("jwt")
public class JWTConfig {
    private String secret;
    private Long expiration;
    private String header;
    private String prefix;
    private String suffix;
    private Long accessTokenLife;
    private Long refreshTokenLife;
}
