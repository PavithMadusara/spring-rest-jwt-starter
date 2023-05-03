package com.aupma.spring.starter.security.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
public class UserDTO implements UserDetails {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String username;

    @NotNull
    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    private String email;

    private String phone;

    private Boolean isEmailVerified;

    private Boolean isPhoneVerified;

    private Boolean isTotpVerified;

    private Boolean isTempPassword;

    private Boolean isMfaEnabled;

    private Boolean isBanned;

    private Boolean isApproved;

    private List<RoleDTO> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> role.getAuthorities().forEach(authority ->
                authorities.add((GrantedAuthority) authority::getCode))
        );
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isApproved;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAccountNonExpired();
    }

}
