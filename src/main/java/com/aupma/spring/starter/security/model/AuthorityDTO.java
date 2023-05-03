package com.aupma.spring.starter.security.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthorityDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String description;

}
