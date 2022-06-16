package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.entity.Permission;
import com.aupma.spring.starter.security.entity.Role;
import com.aupma.spring.starter.security.model.PermissionDTO;
import com.aupma.spring.starter.security.model.RoleDTO;
import com.aupma.spring.starter.security.repos.PermissionRepository;
import com.aupma.spring.starter.security.repos.RoleRepository;
import com.aupma.spring.starter.security.util.SimplePage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;

    public SimplePage<RoleDTO> paginate(final Pageable pageable) {
        final Page<Role> page = roleRepository.findAll(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(role -> mapToDTO(role, new RoleDTO()))
                .collect(Collectors.toList()),
                page.getTotalElements(), pageable);
    }
    public List<RoleDTO> findAll() {
        return roleRepository.findAll().stream().map(role -> mapToDTO(role, new RoleDTO()))
                .collect(Collectors.toList());
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
        if (role.getPermissions() != null) {
            roleDTO.setPermissions(role.getPermissions().stream()
                    .map(permission -> permissionService.mapToDTO(permission, new PermissionDTO()))
                    .collect(Collectors.toList()));
        }
        return roleDTO;
    }

    public void mapToEntity(final RoleDTO roleDTO, final Role role) {
        role.setName(roleDTO.getName());
        role.setLevel(roleDTO.getLevel());
        if (roleDTO.getPermissions() != null) {
            final List<Permission> permissions = permissionRepository.findAllById(roleDTO.getPermissions().stream()
                    .map(PermissionDTO::getId).collect(Collectors.toList()));
            if (permissions.size() != roleDTO.getPermissions().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of permissions not found");
            }
            role.setPermissions(new HashSet<>(permissions));
        }
    }

}
