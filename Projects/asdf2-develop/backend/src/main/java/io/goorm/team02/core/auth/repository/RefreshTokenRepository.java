package io.goorm.team02.core.auth.repository;

import io.goorm.team02.core.auth.domain.RefreshToken;
import io.goorm.team02.core.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    void deleteByUserId(Long userId);
}
