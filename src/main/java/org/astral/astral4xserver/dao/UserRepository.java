package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);

    boolean existsByUsername(String username);
}
