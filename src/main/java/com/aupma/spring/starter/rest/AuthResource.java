package com.aupma.spring.starter.rest;

import com.aupma.spring.starter.entity.Role;
import com.aupma.spring.starter.model.AuthRequestDTO;
import com.aupma.spring.starter.model.AuthResponseDTO;
import com.aupma.spring.starter.model.TokenRequestDTO;
import com.aupma.spring.starter.model.UserDTO;
import com.aupma.spring.starter.service.JwtTokenService;
import com.aupma.spring.starter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthResource {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            User user = (User) authenticate.getPrincipal();

            AuthResponseDTO authResponse = new AuthResponseDTO();

            boolean isMfaEnabled = userService.getIsMfaEnabled(user.getUsername());

            if (isMfaEnabled) {
                authResponse.setIsMfaRequired(true);
                // Generating access token without any roles, this can be only used for MFA verification request
                authResponse.setAccessToken(tokenService.generateAccessToken(user, new ArrayList<>()));
            } else {
                Set<Role> roles = userService.getRoles(user.getUsername());
                authResponse.setIsMfaRequired(false);
                authResponse.setAccessToken(tokenService.generateAccessToken(user, roles.stream().toList()));
                authResponse.setRefreshToken(tokenService.generateRefreshToken(user));
            }

            authResponse.setExpiresIn(tokenService.getExpiredDateFromToken(authResponse.getAccessToken()));

            return ResponseEntity.ok().body(authResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        String bearerToken = token.substring(7);
        String username = tokenService.getUserNameFromToken(bearerToken);
        com.aupma.spring.starter.entity.User user = userService.getUser(username);
        return ResponseEntity.ok().body(userService.mapToDTO(user, new UserDTO()));
    }

    @PostMapping("/token-refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody TokenRequestDTO requestDTO) {
        UserDetails userDetails = userService.loadUserByUsername(tokenService.getUserNameFromToken(requestDTO.getRefreshToken()));
        Set<Role> roles = userService.getRoles(userDetails.getUsername());
        boolean isValidToken = tokenService.validateToken(requestDTO.getRefreshToken(), userDetails);
        if (isValidToken) {
            String newAccessToken = tokenService.generateAccessToken(userDetails, roles.stream().toList());
            AuthResponseDTO authResponse = new AuthResponseDTO();
            authResponse.setAccessToken(newAccessToken);
            authResponse.setRefreshToken(requestDTO.getRefreshToken());
            authResponse.setExpiresIn(tokenService.getExpiredDateFromToken(newAccessToken));
            return ResponseEntity.ok().body(authResponse);
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(null);
        }
    }
}
