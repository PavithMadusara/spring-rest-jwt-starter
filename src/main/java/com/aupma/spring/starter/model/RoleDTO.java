package com.aupma.spring.starter.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Getter
@Setter
public class RoleDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private Integer level;

    private List<PermissionDTO> permissions;

}
