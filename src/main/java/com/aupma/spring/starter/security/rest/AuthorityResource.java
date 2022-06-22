package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.model.AuthorityDTO;
import com.aupma.spring.starter.security.service.AuthorityService;
import com.aupma.spring.starter.security.util.Authorities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/authorities", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthorityResource {

    private final AuthorityService authorityService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('" + Authorities.READ_AUTHORITY + "')")
    public ResponseEntity<List<AuthorityDTO>> getAllAuthorities() {
        return ResponseEntity.ok(authorityService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.UPDATE_AUTHORITY + "')")
    public ResponseEntity<AuthorityDTO> updateAuthority(
            @PathVariable final Long id,
            @RequestBody @Valid final AuthorityDTO authorityDTO
    ) {
        return ResponseEntity.ok(this.authorityService.update(id, authorityDTO));
    }

}
