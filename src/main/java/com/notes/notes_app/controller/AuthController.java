package com.notes.notes_app.controller;

import com.notes.notes_app.errorHandle.InvalidCredentialsException;
import com.notes.notes_app.model.User;
import com.notes.notes_app.security.JwtUtility;
import com.notes.notes_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private JwtUtility jwtUtility;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User request) {
        User user = userService.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtUtility.generateToken(request.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User request) {
        User createdUser = userService.createUser(request);
        String token = jwtUtility.generateToken(request.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("token", token);
        response.put("userId", createdUser.getId());
        return ResponseEntity.ok(response);
    }
}
