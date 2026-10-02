package com.smartdine.backend.controller;

import com.smartdine.backend.dto.LoginRequest;
import com.smartdine.backend.dto.LoginResponse;
import com.smartdine.backend.entity.User;
import com.smartdine.backend.repository.UserRepository;
import com.smartdine.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Password is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);

        savedUser.setPassword(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Password is required");
        }

        return userRepository.findByEmail(request.getEmail())
                .map(user -> {

                    if (!passwordEncoder.matches(
                            request.getPassword(),
                            user.getPassword()
                    )) {
                        return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid email or password");
                    }

                    String token = jwtService.generateToken(
                            user.getId(),
                            user.getEmail(),
                            user.getRole()
                    );

                    user.setPassword(null);

                    LoginResponse response =
                            new LoginResponse(token, user);

                    return ResponseEntity.ok(response);
                })
                .orElse(
                        ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid email or password")
                );
    }
}