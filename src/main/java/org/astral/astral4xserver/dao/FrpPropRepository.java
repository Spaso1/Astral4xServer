package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.FrpProp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FrpPropRepository extends JpaRepository<FrpProp, Long> {
    Optional<FrpProp> findByName(String name);
    List<FrpProp> findByUserId(int userId);
    void deleteById(@NotNull Long id);
    @Modifying
    @Transactional
    @Query("UPDATE FrpProp f SET f.status = :status WHERE f.name = :name")
    void updateStatusById(String name, String status);
    @Query("UPDATE FrpProp f SET f.status = :status")
    void updateStatusAll(String status);
    @Query("UPDATE FrpProp f SET f.stream = :stream WHERE f.name = :name")
    void updateStreamByName(String name, long stream);
    @Query("UPDATE FrpProp f SET f.streamTotal = f.streamTotal + :increment WHERE f.name = :name")
    void updateStreamTotalByName(String name, long increment);
    // Custom query methods can be defined here
}