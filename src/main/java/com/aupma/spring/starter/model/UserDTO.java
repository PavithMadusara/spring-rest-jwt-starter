package com.aupma.spring.starter.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Getter
@Setter
public class UserDTO {

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

    private Boolean isTempPassword;

    private Boolean isMfaEnabled;

    private Boolean isBanned;

    private Boolean isApproved;

    private List<RoleDTO> roles;

}
