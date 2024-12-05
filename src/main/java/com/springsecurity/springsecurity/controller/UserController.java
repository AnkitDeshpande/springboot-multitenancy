package com.springsecurity.springsecurity.controller;

import com.springsecurity.springsecurity.dto.CreateUserRequestDto;
import com.springsecurity.springsecurity.entity.User;
import com.springsecurity.springsecurity.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsServiceImpl userService;

    @PostMapping("/save")
    public ResponseEntity<User> addUserWithRoleAdmin(@RequestBody CreateUserRequestDto createUserRequestDto) {
        return ResponseEntity.ok(userService.saveUser(createUserRequestDto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());

    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateUser(@RequestParam("email") String email,
                                               @RequestParam("token") String token,
                                               @RequestParam("expiresAt") String expiresAt) {
        boolean isActivated = userService.activateUser(email, token, expiresAt);
        if (isActivated) {
            return ResponseEntity.ok("User activated successfully.");
        } else {
            return ResponseEntity.status(400).body("Activation link is invalid or expired.");
        }
    }

}
