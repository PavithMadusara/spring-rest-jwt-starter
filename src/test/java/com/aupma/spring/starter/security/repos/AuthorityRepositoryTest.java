package com.aupma.spring.starter.security.repos;

import com.aupma.spring.starter.security.entity.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorityRepositoryTest {

    @Autowired
    private AuthorityRepository underTest;

    @Test
    void findByCode() {
        // given
        String authorityCode = "TEST_AUTHORITY";
        Authority authority = new Authority();
        authority.setCode(authorityCode);
        underTest.save(authority);

        // when
        Authority found = underTest.findByCode(authorityCode);

        // then
        assertNotEquals(found,null);
    }
}
