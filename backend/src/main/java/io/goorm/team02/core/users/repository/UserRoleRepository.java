// UserRoleRepository.java
package io.goorm.team02.core.users.repository;

import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import io.goorm.team02.core.users.domain.User;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    //user_id와 role이 일치하는 UserRole을 DB에서 조회
    //Optional<UserRole> findByUserIdAndRole(Long userId, UserRole.Role role);
    Optional<UserRole> findByUserIdAndRole(Long userId, String role);
    List<UserRole> findByUserId(Long userId);
    // 특정 사용자 모든 역할 조회
    //List<UserRole> findByUser(User user);
}
