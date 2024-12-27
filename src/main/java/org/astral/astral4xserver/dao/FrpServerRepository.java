package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.FrpServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrpServerRepository extends JpaRepository<FrpServer, Long> {
    @NotNull
    List<FrpServer> findAll();
}
