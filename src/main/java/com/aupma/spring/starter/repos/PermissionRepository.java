package com.aupma.spring.starter.repos;


import com.aupma.spring.starter.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByCode(String code);
}
