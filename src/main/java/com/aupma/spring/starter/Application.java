package com.aupma.spring.starter;

import com.aupma.spring.starter.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    public static class AppStartupRunner implements CommandLineRunner {
        @Autowired
        private UserService userService;
        @Value("${initializer.enabled}")
        private boolean initializerEnabled;

        @Override
        public void run(String... args) throws Exception {
            userService.syncPermissionToDatabase();
            if (initializerEnabled) {
                userService.createAdminIfNotExists();
            }
        }
    }

}
