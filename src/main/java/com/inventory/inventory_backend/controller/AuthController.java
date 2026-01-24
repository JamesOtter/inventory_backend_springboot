package com.inventory.inventory_backend.controller;

import com.inventory.inventory_backend.model.ERole;
import com.inventory.inventory_backend.model.User;
import com.inventory.inventory_backend.repository.UserRepository;
import com.inventory.inventory_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user)
    {
        if(user.getEmail() == null || user.getPassword() == null || user.getUsername() == null) {
            return "Email, username and password are required";
        }
        if(userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRole() == null) {
            user.setRole(ERole.ROLE_USER);
        }

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request){

        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        return Map.of("token", token);
    }
}
