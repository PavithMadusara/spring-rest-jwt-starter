package com.aupma.spring.starter.security.entity;

import com.aupma.spring.starter.security.model.VerificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class VerificationCode {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationType type;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    @PrePersist
    public void onCreate() {
        dateCreated = lastUpdated = OffsetDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdated = OffsetDateTime.now();
    }

}
