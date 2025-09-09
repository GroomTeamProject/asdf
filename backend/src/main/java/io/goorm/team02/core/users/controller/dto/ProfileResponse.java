package io.goorm.team02.core.users.controller.dto;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.enums.UserType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private String email;
    private String name;
    private String phone;
    private UserType userType;

    public static ProfileResponse fromEntity(User user) {
        return ProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .build();
    }
}