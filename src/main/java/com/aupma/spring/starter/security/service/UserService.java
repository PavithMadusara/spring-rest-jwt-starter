package com.aupma.spring.starter.security.service;

import com.aupma.spring.starter.security.entity.Authority;
import com.aupma.spring.starter.security.entity.Role;
import com.aupma.spring.starter.security.entity.User;
import com.aupma.spring.starter.security.model.AuthorityDTO;
import com.aupma.spring.starter.security.model.RoleDTO;
import com.aupma.spring.starter.security.model.UserDTO;
import com.aupma.spring.starter.security.repos.AuthorityRepository;
import com.aupma.spring.starter.security.repos.RoleRepository;
import com.aupma.spring.starter.security.repos.UserRepository;
import com.aupma.spring.starter.security.util.Authorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final AuthorityService authorityService;
    private final TotpService totpService;

    @Value("${initializer.username}")
    private String adminUsername;
    @Value("${initializer.password}")
    private String adminPassword;

    @Bean
    public Function<UserDetails, User> fetchCurrentUser() {
        return user -> getUser(user.getUsername());
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public Page<UserDTO> paginate(final Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> mapToDTO(user, new UserDTO()));
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

        user.setIsTotpVerified(false);
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);
        user.setIsMfaEnabled(false);
        user.setMfaSecret(totpService.generateSecret());
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
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setIsEmailVerified(user.getIsEmailVerified());
        userDTO.setIsPhoneVerified(user.getIsPhoneVerified());
        userDTO.setIsTempPassword(user.getIsTempPassword());
        userDTO.setIsBanned(user.getIsBanned());
        userDTO.setIsTotpVerified(user.getIsTotpVerified());
        userDTO.setIsMfaEnabled(user.getIsMfaEnabled());
        userDTO.setIsApproved(user.getIsApproved());
        userDTO.setRoles(user.getRoles() == null ? null : user.getRoles().stream()
                .map(role -> roleService.mapToDTO(role, new RoleDTO())).toList());
        return userDTO;
    }

    public void mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setIsEmailVerified(userDTO.getIsEmailVerified());
        user.setIsPhoneVerified(userDTO.getIsPhoneVerified());
        user.setIsTempPassword(userDTO.getIsTempPassword());
        user.setIsTotpVerified(userDTO.getIsTotpVerified());
        user.setIsMfaEnabled(userDTO.getIsMfaEnabled());
        user.setIsBanned(userDTO.getIsBanned());
        user.setIsApproved(userDTO.getIsApproved());
        if (userDTO.getRoles() != null) {
            final List<Role> roles = roleRepository.findAllById(userDTO.getRoles().stream().map(RoleDTO::getId)
                    .toList());
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
            throw new UsernameNotFoundException(username);
        }
        UserDTO dto = mapToDTO(user, new UserDTO());
        dto.setPassword(user.getPassword());
        return dto;
    }

    public Set<Role> getRoles(String username) {
        User user = userRepository.findByUsername(username);
        return user.getRoles();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void syncAuthoritiesToDatabase() throws IllegalAccessException {
        for (Field field : Authorities.class.getFields()) {
            Object target = new Object();
            String value = (String) field.get(target);

            AuthorityDTO authorityDTO = new AuthorityDTO();
            authorityDTO.setCode(value);
            authorityDTO.setDescription(value);

            authorityService.create(authorityDTO);
        }
    }

    public void syncAuthoritiesToSuperAdmin() {
        Role superAdmin = roleRepository.findByName("SUPER_ADMIN");
        if (superAdmin != null) {
            List<Authority> allAuthorities = authorityRepository.findAll();
            superAdmin.setAuthorities(new HashSet<>(allAuthorities));
            roleRepository.save(superAdmin);
        }
    }

    public void createAdminIfNotExists() {

        List<User> admins = userRepository.findByRoleName("SUPER_ADMIN");
        if (admins.isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setLevel(0);
            Role admin = roleRepository.findByName("SUPER_ADMIN");
            List<Authority> allAuthorities = authorityRepository.findAll();

            if (admin != null) {
                adminRole = admin;
                if (adminRole.getAuthorities().size() < allAuthorities.size()) {
                    adminRole.setAuthorities(new HashSet<>(allAuthorities));
                    roleRepository.save(adminRole);
                }
            } else {
                adminRole.setAuthorities(new HashSet<>(allAuthorities));
                roleRepository.save(adminRole);
            }

            final User user = userRepository.findByUsername(adminUsername);
            if (user == null) {
                final UserDTO userDTO = new UserDTO();
                userDTO.setUsername(adminUsername);
                userDTO.setPassword(adminPassword);
                userDTO.setFirstName("Super");
                userDTO.setLastName("Admin");
                userDTO.setEmail(adminUsername);
                userDTO.setIsBanned(false);
                userDTO.setIsApproved(true);
                userDTO.setIsTempPassword(true);
                userDTO.setRoles(new ArrayList<>());
                userDTO.getRoles().add(roleService.get(adminRole.getId()));
                create(userDTO);
            }
        }
    }

    public boolean getIsMfaEnabled(String username) {
        User user = userRepository.findByUsername(username);
        return user.getIsMfaEnabled();
    }

    public void enableTotp(String username) {
        User user = userRepository.findByUsername(username);
        user.setIsMfaEnabled(true);
        user.setIsTotpVerified(true);
        userRepository.save(user);
    }

    public void enablePhone(String username) {
        User user = userRepository.findByUsername(username);
        user.setIsMfaEnabled(true);
        user.setIsPhoneVerified(true);
        userRepository.save(user);
    }

    public void updatePassword(Long id, String newPassword) {
        userRepository.findById(id).ifPresent(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);
        });
    }
}
