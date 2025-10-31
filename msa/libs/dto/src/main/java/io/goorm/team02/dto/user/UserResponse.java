package io.goorm.team02.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String phone,
        String name,
        LocalDate birthDate,
        String userType,
        Boolean isActive,
        Boolean emailVerified,
        Boolean phoneVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
