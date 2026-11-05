package com.Samrajya.MakeOver.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Samrajya.MakeOver.Entity.User;
import com.Samrajya.MakeOver.Interfaces.UserRepository;

/**
 * Creates (or promotes) the admin account from ADMIN_EMAIL / ADMIN_PASSWORD.
 * Admins can never be created through the public /auth/register endpoint.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public AdminSeeder(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            System.out.println("[AdminSeeder] ADMIN_EMAIL / ADMIN_PASSWORD not set - no admin created.");
            return;
        }
        User admin = users.findByEmail(adminEmail).orElseGet(() -> {
            User u = new User();
            u.setName("Admin");
            u.setEmail(adminEmail);
            u.setPassword(encoder.encode(adminPassword));
            u.setMobile("-");
            u.setAddress("-");
            u.setCity("-");
            return u;
        });
        admin.setRole("ADMIN");
        users.save(admin);
        System.out.println("[AdminSeeder] Admin account ready: " + adminEmail);
    }
}
