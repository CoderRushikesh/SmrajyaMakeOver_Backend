package com.Samrajya.MakeOver.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Samrajya.MakeOver.Entity.User;
import com.Samrajya.MakeOver.Interfaces.UserRepository;
import com.Samrajya.MakeOver.Service.JwtService;
import com.Samrajya.MakeOver.dto.LoginRequestDTO;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            // SECURITY: never trust role/id/createdAt sent by the client
            user.setId(null);
            user.setRole("USER");
            user.setCreatedAt(null);
            user.setPassword(encoder.encode(user.getPassword()));
            repo.save(user);
            return ResponseEntity.ok("User registered!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed (email may already exist)");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            User dbUser = repo.findByEmail(request.getEmail())
                              .orElseThrow(() -> new RuntimeException("User not found"));

            if (encoder.matches(request.getPassword(), dbUser.getPassword())) {
                String role = dbUser.getRole() == null ? "USER" : dbUser.getRole();
                String token = jwtService.generateToken(dbUser.getEmail(), role);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("token", token);
                body.put("role", role);
                body.put("name", dbUser.getName());
                return ResponseEntity.ok(body);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
