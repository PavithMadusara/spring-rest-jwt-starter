package com.aupma.spring.starter.service;

import com.aupma.spring.starter.entity.Permission;
import com.aupma.spring.starter.entity.Role;
import com.aupma.spring.starter.entity.User;
import com.aupma.spring.starter.model.RoleDTO;
import com.aupma.spring.starter.model.UserDTO;
import com.aupma.spring.starter.repos.PermissionRepository;
import com.aupma.spring.starter.repos.RoleRepository;
import com.aupma.spring.starter.repos.UserRepository;
import com.aupma.spring.starter.util.SimplePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
    @Value("${jwt.adminUsername}")
    private String adminUsername;
    @Value("${jwt.adminPassword}")
    private String adminPassword;


    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    public SimplePage<UserDTO> paginate(final Pageable pageable) {
        final Page<User> page = userRepository.findAll(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList()),
                page.getTotalElements(), pageable);
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public User getByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    public UserDTO create(final UserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        final User user = new User();
        mapToEntity(userDTO, user);
        return mapToDTO(userRepository.save(user), new UserDTO());
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setIsTempPassword(user.getIsTempPassword());
        userDTO.setIsBanned(user.getIsBanned());
        userDTO.setIsApproved(user.getIsApproved());
        userDTO.setRoles(user.getRoles() == null ? null : user.getRoles().stream()
                .map(role -> roleService.mapToDTO(role, new RoleDTO())).collect(Collectors.toList()));
        return userDTO;
    }

    public void mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setIsTempPassword(userDTO.getIsTempPassword());
        user.setIsBanned(userDTO.getIsBanned());
        user.setIsApproved(userDTO.getIsApproved());
        if (userDTO.getRoles() != null) {
            final List<Role> roles = roleRepository.findAllById(userDTO.getRoles().stream().map(RoleDTO::getId)
                    .collect(Collectors.toList()));
            if (roles.size() != userDTO.getRoles().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of roles not found");
            }
            user.setRoles(new HashSet<>(roles));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException(username);
        } else {
            log.info("User found: {}", username);
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions()
                            .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getCode())));
                }
            });
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public void createAdminIfNotExists() {

        List<User> admins = userRepository.findByRoles_Name("SUPER_ADMIN");
        if (admins.size() == 0) {
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            Role admin = roleRepository.findByName("SUPER_ADMIN");
            List<Permission> allPermissions = permissionRepository.findAll();

            if (admin != null) {
                adminRole = admin;
                if (adminRole.getPermissions().size() < allPermissions.size()) {
                    adminRole.setPermissions(new HashSet<>(allPermissions));
                    roleRepository.save(adminRole);
                }
            } else {
                adminRole.setPermissions(new HashSet<>(allPermissions));
                roleRepository.save(adminRole);
            }

            final User user = userRepository.findByUsername(adminUsername);
            if (user == null) {
                final UserDTO userDTO = new UserDTO();
                userDTO.setUsername(adminUsername);
                userDTO.setPassword(adminPassword);
                userDTO.setFirstName("Super");
                userDTO.setLastName("Admin");
                userDTO.setIsBanned(false);
                userDTO.setIsApproved(true);
                userDTO.setIsTempPassword(true);
                userDTO.setRoles(new ArrayList<>());
                userDTO.getRoles().add(roleService.get(adminRole.getId()));
                create(userDTO);
            }
        }


    }

    public Set<Role> getRoles(String username) {
        User user = userRepository.findByUsername(username);
        return user.getRoles();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
