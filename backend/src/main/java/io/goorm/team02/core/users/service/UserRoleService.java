// UserRoleService.java
package io.goorm.team02.core.users.service;

import io.goorm.team02.core.users.controller.dto.UserRoleAddResponse;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.UserRole;
import io.goorm.team02.core.users.domain.enums.UserType;
import io.goorm.team02.core.users.repository.UserinfoRepository;
import io.goorm.team02.core.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserinfoRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserRoleAddResponse addRoleToUser(Long userId, String role) {
        // 1. 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2. 이미 역할이 있는지 확인
        userRoleRepository.findByUserIdAndRole(userId, role)
                .ifPresent(r -> {
                    throw new IllegalStateException("User already has this role: " + role);
                });

        // 3. 새로운 역할 저장
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        // 사용자의 모든 역할 조회
        List<String> roleList = userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRole)
                .toList();

        //return userRoleRepository.save(userRole);
        return new UserRoleAddResponse(user.getId(), user.getEmail(), user.getName(), roleList);
    }

}
