package com.aupma.spring.starter.service;

import com.aupma.spring.starter.entity.Permission;
import com.aupma.spring.starter.model.PermissionDTO;
import com.aupma.spring.starter.repos.PermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(final PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionDTO> findAll() {
        return permissionRepository.findAll()
                .stream()
                .map(permission -> mapToDTO(permission, new PermissionDTO()))
                .collect(Collectors.toList());
    }

    public PermissionDTO get(final Long id) {
        return permissionRepository.findById(id)
                .map(permission -> mapToDTO(permission, new PermissionDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void create(final PermissionDTO permissionDTO) {
        Permission authority = permissionRepository.findByCode(permissionDTO.getCode());
        if (authority == null) {
            final Permission permission = new Permission();
            mapToEntity(permissionDTO, permission);
            permissionRepository.save(permission);
        }
    }

    public PermissionDTO update(final Long id, final PermissionDTO permissionDTO) {
        Permission authority = permissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(permissionDTO, authority);
        return mapToDTO(permissionRepository.save(authority), new PermissionDTO());
    }

    public PermissionDTO mapToDTO(final Permission permission, final PermissionDTO permissionDTO) {
        permissionDTO.setId(permission.getId());
        permissionDTO.setCode(permission.getCode());
        permissionDTO.setDescription(permission.getDescription());
        return permissionDTO;
    }

    public void mapToEntity(final PermissionDTO permissionDTO, final Permission permission) {
        permission.setCode(permissionDTO.getCode());
        permission.setDescription(permissionDTO.getDescription());
    }

}
