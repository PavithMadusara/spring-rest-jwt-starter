package com.aupma.spring.starter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration()
@ConfigurationProperties("initializer")
public class InitializerConfig {
    private String username;
    private String password;
    private Boolean enabled;
}
