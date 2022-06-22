package com.aupma.spring.starter.security.config;

import com.aupma.spring.starter.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Value("${initializer.enabled}")
    private boolean initializerEnabled;

    @Override
    public void run(String... args) throws Exception {
        userService.syncAuthoritiesToDatabase();
        if (initializerEnabled) {
            userService.createAdminIfNotExists();
            userService.syncAuthoritiesToSuperAdmin();
        }
    }
}
