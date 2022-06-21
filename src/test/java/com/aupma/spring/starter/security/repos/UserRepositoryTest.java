package com.aupma.spring.starter.security.repos;

import com.aupma.spring.starter.security.entity.Role;
import com.aupma.spring.starter.security.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void findByUsername() {
        // given
        String username = "test-user";

        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setIsEmailVerified(false);
        user.setIsTotpVerified(false);
        user.setIsPhoneVerified(false);
        underTest.save(user);

        // when
        User userFound = underTest.findByUsername(username);

        // then
        assertEquals(username, userFound.getUsername());
    }

    @Test
    void findByRoles_Name() {
        // given
        String username = "test-user";
        String roleName = "test-role";

        User user1 = new User();
        user1.setUsername(username);
        user1.setPassword("password");
        user1.setIsEmailVerified(false);
        user1.setIsTotpVerified(false);
        user1.setIsPhoneVerified(false);

        User user2 = new User();
        user2.setUsername("test-user-2");
        user2.setPassword("password");
        user2.setIsEmailVerified(false);
        user2.setIsTotpVerified(false);
        user2.setIsPhoneVerified(false);
        underTest.save(user2);

        Role role = new Role();
        role.setName(roleName);
        role.setLevel(0);

        user1.setRoles(new HashSet<>(List.of(role)));
        underTest.save(user1);

        // when
        List<User> users = underTest.findByRoles_Name(roleName);

        // then
        assertEquals(1, users.size());
    }
}
