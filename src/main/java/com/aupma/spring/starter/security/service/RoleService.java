package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.entity.Authority;
import com.aupma.spring.starter.security.entity.Role;
import com.aupma.spring.starter.security.model.AuthorityDTO;
import com.aupma.spring.starter.security.model.RoleDTO;
import com.aupma.spring.starter.security.repos.AuthorityRepository;
import com.aupma.spring.starter.security.repos.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final AuthorityService authorityService;

    public Page<RoleDTO> paginate(final Pageable pageable) {
        return roleRepository.findAll(pageable).map(role -> mapToDTO(role, new RoleDTO()));
    }

    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream().map(role -> mapToDTO(role, new RoleDTO()))
                .toList();
    }

    public RoleDTO get(final Long id) {
        return roleRepository.findById(id).map(role -> mapToDTO(role, new RoleDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final RoleDTO roleDTO) {
        final Role role = new Role();
        mapToEntity(roleDTO, role);
        return roleRepository.save(role).getId();
    }

    public void update(final Long id, final RoleDTO roleDTO) {
        final Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(roleDTO, role);
        roleRepository.save(role);
    }

    public void delete(final Long id) {
        roleRepository.deleteById(id);
    }

    public RoleDTO mapToDTO(final Role role, final RoleDTO roleDTO) {
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setLevel(role.getLevel());
        if (role.getAuthorities() != null) {
            roleDTO.setAuthorities(role.getAuthorities().stream()
                    .map(authority -> authorityService.mapToDTO(authority, new AuthorityDTO()))
                    .toList());
        }
        return roleDTO;
    }

    public void mapToEntity(final RoleDTO roleDTO, final Role role) {
        role.setName(roleDTO.getName());
        role.setLevel(roleDTO.getLevel());
        if (roleDTO.getAuthorities() != null) {
            final List<Authority> authorities = authorityRepository.findAllById(roleDTO.getAuthorities().stream()
                    .map(AuthorityDTO::getId).toList());
            if (authorities.size() != roleDTO.getAuthorities().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of authorities not found");
            }
            role.setAuthorities(new HashSet<>(authorities));
        }
    }

}
