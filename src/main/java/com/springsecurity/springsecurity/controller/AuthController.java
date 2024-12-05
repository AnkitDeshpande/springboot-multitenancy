package com.springsecurity.springsecurity.controller;

import com.springsecurity.springsecurity.dto.JwtResponseDto;
import com.springsecurity.springsecurity.entity.User;
import com.springsecurity.springsecurity.repository.UserRepository;
import com.springsecurity.springsecurity.service.impl.UserDetailsServiceImpl;
import com.springsecurity.springsecurity.dto.AuthRequestDto;
import com.springsecurity.springsecurity.dto.CreateUserRequestDto;
import com.springsecurity.springsecurity.models.TenantContext;
import com.springsecurity.springsecurity.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
            if (authentication.isAuthenticated()) {

                String token = jwtService.generateToken(authRequestDTO.getUsername());
                String tenant = userRepo.findByUsername(authRequestDTO.getUsername()).getSchemaName();
                TenantContext.setCurrentTenant(tenant);
                return ResponseEntity.ok(JwtResponseDto.builder()
                        .accessToken(token)
                        .username(authentication.getName())
                        .build());
            } else {
                throw new UsernameNotFoundException("invalid user request..!!");
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        User registeredUser = userDetailsService.saveUser(createUserRequestDto);
        return ResponseEntity.ok(registeredUser);
    }

}
