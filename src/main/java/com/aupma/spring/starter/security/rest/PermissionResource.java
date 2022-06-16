package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.model.PermissionDTO;
import com.aupma.spring.starter.security.service.PermissionService;
import com.aupma.spring.starter.security.util.Authorities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PermissionResource {

    private final PermissionService permissionService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('" + Authorities.READ_PERMISSION + "')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.UPDATE_PERMISSION + "')")
    public ResponseEntity<PermissionDTO> updatePermission(
            @PathVariable final Long id,
            @RequestBody @Valid final PermissionDTO permissionDTO
    ) {
        return ResponseEntity.ok(this.permissionService.update(id, permissionDTO));
    }

}
