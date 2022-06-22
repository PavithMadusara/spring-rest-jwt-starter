package com.aupma.spring.starter.security.repos;


import com.aupma.spring.starter.security.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByCode(String code);
}
