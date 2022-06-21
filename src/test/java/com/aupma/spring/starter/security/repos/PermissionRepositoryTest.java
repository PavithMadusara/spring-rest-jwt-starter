package com.aupma.spring.starter.security.repos;

import com.aupma.spring.starter.security.entity.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository underTest;

    @Test
    void findByCode() {
        // given
        String permissionCode = "TEST_PERMISSION";
        Permission permission = new Permission();
        permission.setCode(permissionCode);
        underTest.save(permission);

        // when
        Permission found = underTest.findByCode(permissionCode);

        // then
        assertNotEquals(found,null);
    }
}
