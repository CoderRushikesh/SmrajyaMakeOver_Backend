package com.Samrajya.MakeOver.Controller;

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
            user.setPassword(encoder.encode(user.getPassword()));
            repo.save(user);
            return ResponseEntity.ok("User registered!");
        } catch (Exception e) {
            e.printStackTrace(); // log error in console
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            User dbUser = repo.findByEmail(request.getEmail())
                              .orElseThrow(() -> new RuntimeException("User not found"));

            if (encoder.matches(request.getPassword(), dbUser.getPassword())) {
                String token = jwtService.generateToken(dbUser.getEmail());
                return ResponseEntity.ok(Map.of("token", token));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Login failed: " + e.getMessage());
        }
    }


}
