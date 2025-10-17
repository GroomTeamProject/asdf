package io.goorm.team02.core.users.controller.dto;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String email;
    private String phone;
    private String name;
    private LocalDate birthDate;
    private UserType userType;
    private Boolean isActive;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User 엔티티에서 UserResponse로 변환
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                //.phone(formatPhone(user.getPhone())) 
                .phone(user.getPhone())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .userType(user.getUserType())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    // 전화번호 포맷팅 메서드
    private static String formatPhone(String phone) {
        if (phone == null) return null;

        // 11자리일 때만 010-xxxx-xxxx 형식
        if (phone.length() == 11) {
            return phone.substring(0, 3) + "-" 
                + phone.substring(3, 7) + "-" 
                + phone.substring(7);
        }

        // 10자리일 때 02-xxxx-xxxx 등 처리
        if (phone.length() == 10) {
            return phone.substring(0, 3) + "-" 
                + phone.substring(3, 6) + "-" 
                + phone.substring(6);
        }

        // 길이가 맞지 않으면 그대로 반환
        return phone;
    }
}
