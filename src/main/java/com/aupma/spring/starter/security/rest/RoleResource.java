package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.model.RoleDTO;
import com.aupma.spring.starter.security.service.RoleService;
import com.aupma.spring.starter.security.util.Authorities;
import com.aupma.spring.starter.security.util.SimplePage;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleResource {

    private final RoleService roleService;

    public RoleResource(final RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/paginate")
    public ResponseEntity<SimplePage<RoleDTO>> paginateRoles(
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            @PageableDefault(size = 15) final Pageable pageable
    ) {
        return ResponseEntity.ok(roleService.paginate(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable final Long id) {
        return ResponseEntity.ok(roleService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Authorities.CREATE_ROLE + "')")
    public ResponseEntity<Long> createRole(@RequestBody @Valid final RoleDTO roleDTO) {
        return new ResponseEntity<>(roleService.create(roleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.UPDATE_ROLE + "')")
    public ResponseEntity<Void> updateRole(
            @PathVariable final Long id,
            @RequestBody @Valid final RoleDTO roleDTO
    ) {
        roleService.update(id, roleDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.DELETE_ROLE + "')")
    public ResponseEntity<Void> deleteRole(@PathVariable final Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
