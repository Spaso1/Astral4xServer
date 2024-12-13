package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.FrpProp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FrpPropRepository extends JpaRepository<FrpProp, Long> {
    Optional<FrpProp> findByName(String name);
    List<FrpProp> findByUserId(int userId);

    // Custom query methods can be defined here
}