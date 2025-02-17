package com.notes.notes_app.controller;

import com.notes.notes_app.errorHandle.InvalidCredentialsException;
import com.notes.notes_app.model.User;
import com.notes.notes_app.security.JwtUtility;
import com.notes.notes_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private JwtUtility jwtUtility;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private UserService userService;

    // Move the business logic to AuthService
    @PostMapping("/login")
    public String login(@RequestBody User request) {
        User user = userService.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return jwtUtility.generateToken(request.getUsername());
    }

    // Add the token in return so the FE can save the jwt on login
    @PostMapping("/register")
    public String register(@RequestBody User request) {
        return userService.createUser(request);
    }
}
