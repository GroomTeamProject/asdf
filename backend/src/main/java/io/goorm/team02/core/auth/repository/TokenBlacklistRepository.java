package io.goorm.team02.core.auth.repository;

import io.goorm.team02.core.auth.domain.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByJti(String jti);
    void deleteByExpiresAtBefore(LocalDateTime time);
}
