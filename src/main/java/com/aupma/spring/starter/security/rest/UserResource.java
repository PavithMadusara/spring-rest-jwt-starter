package com.aupma.spring.starter.security.rest;

import com.aupma.spring.starter.security.model.UserDTO;
import com.aupma.spring.starter.security.service.UserService;
import com.aupma.spring.starter.security.util.Authorities;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + Authorities.READ_USER + "')")
    public ResponseEntity<Page<UserDTO>> paginateUsers(
            @SortDefault(sort = "firstName", direction = Sort.Direction.ASC)
            @PageableDefault(size = 15) final Pageable pageable
    ) {
        return ResponseEntity.ok(userService.paginate(pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('" + Authorities.READ_USER + "')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority('" + Authorities.READ_USER + "') or returnObject.body.username == principal.username")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Authorities.CREATE_USER + "')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.UPDATE_USER + "') or #user.username == principal.username")
    public ResponseEntity<Void> updateUser(@PathVariable("id") final Long id, @RequestBody UserDTO user) {
        userService.update(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Authorities.DELETE_USER + "')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") final Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

}

