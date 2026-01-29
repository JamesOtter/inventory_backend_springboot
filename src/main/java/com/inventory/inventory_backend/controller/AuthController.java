package com.inventory.inventory_backend.controller;

import com.inventory.inventory_backend.dto.LoginRequest;
import com.inventory.inventory_backend.dto.RegisterRequest;
import com.inventory.inventory_backend.exception.FieldValidationException;
import com.inventory.inventory_backend.exception.GlobalException;
import com.inventory.inventory_backend.model.ERole;
import com.inventory.inventory_backend.model.User;
import com.inventory.inventory_backend.repository.UserRepository;
import com.inventory.inventory_backend.security.JwtUtils;
import com.inventory.inventory_backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // @Valid - will do the validation in DTO and throw exception ('MethodArgumentNotValidException')
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request)
    {
        log.info("Registration attempt for username={}", request.getUsername());

        String email = request.getEmail();
        String username = request.getUsername();

        if(userRepository.existsByUsername(username)) {
            throw new FieldValidationException("username", "Username is already in use");
        }
        if(userRepository.existsByEmail(email)) {
            throw new FieldValidationException("email", "Email is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ERole.ROLE_USER);

        try{
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to register user {}", user.getUsername(), e);
            throw new FieldValidationException("general", "Failed to create product");
        };

        log.info("User registered successfully with id={}", user.getId());

        return ResponseEntity.ok("User Register Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){

        log.info("Login attempt for email={}", request.getEmail());

        String email = request.getEmail();
        String password = request.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed for email={}", request.getEmail());
                    return new FieldValidationException("general", "Invalid credentials");
                });

        if(!passwordEncoder.matches(password, user.getPassword())){
            log.warn("Login failed for email={}", request.getEmail());
            throw new FieldValidationException("general", "Invalid credentials");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail());

        log.info("Login successful for email={}", request.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok("User is validate");
    }
}
