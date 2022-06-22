package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.entity.Authority;
import com.aupma.spring.starter.security.model.AuthorityDTO;
import com.aupma.spring.starter.security.repos.AuthorityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(final AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public List<AuthorityDTO> findAll() {
        return authorityRepository.findAll()
                .stream()
                .map(authority -> mapToDTO(authority, new AuthorityDTO()))
                .collect(Collectors.toList());
    }

    public AuthorityDTO get(final Long id) {
        return authorityRepository.findById(id)
                .map(authority -> mapToDTO(authority, new AuthorityDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void create(final AuthorityDTO authorityDTO) {
        Authority authority = authorityRepository.findByCode(authorityDTO.getCode());
        if (authority == null) {
            final Authority auth = new Authority();
            mapToEntity(authorityDTO, auth);
            authorityRepository.save(auth);
        }
    }

    public AuthorityDTO update(final Long id, final AuthorityDTO authorityDTO) {
        Authority authority = authorityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(authorityDTO, authority);
        return mapToDTO(authorityRepository.save(authority), new AuthorityDTO());
    }

    public AuthorityDTO mapToDTO(final Authority authority, final AuthorityDTO authorityDTO) {
        authorityDTO.setId(authority.getId());
        authorityDTO.setCode(authority.getCode());
        authorityDTO.setDescription(authority.getDescription());
        return authorityDTO;
    }

    public void mapToEntity(final AuthorityDTO authorityDTO, final Authority authority) {
        authority.setCode(authorityDTO.getCode());
        authority.setDescription(authorityDTO.getDescription());
    }

}
