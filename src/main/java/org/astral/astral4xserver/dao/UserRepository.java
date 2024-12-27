package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    boolean existsByUsername(String username);
    // 新增方法
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.countStream = ?1 WHERE u.id = ?2")
    void updateUserCountStream(long countStream, Long id);

    Optional<User> findByToken(String token);
}
