package com.aupma.spring.starter.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class PermissionDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String description;

}
