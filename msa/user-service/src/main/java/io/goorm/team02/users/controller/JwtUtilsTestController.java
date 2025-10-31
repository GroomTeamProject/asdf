/*package io.goorm.team02.users.controller;

import io.goorm.team02.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class JwtUtilsTestController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        boolean valid = JwtUtils.validateToken(token, jwtSecret);
        Long userId = JwtUtils.getUserIdFromToken(token, jwtSecret);
        String email = JwtUtils.getEmailFromToken(token, jwtSecret);

        return "Valid: " + valid + ", userId: " + userId + ", email: " + email;
    }
}*/