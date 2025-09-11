package io.goorm.team02.core.users.repository;

import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.domain.UserAddress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUser(User user);

}

