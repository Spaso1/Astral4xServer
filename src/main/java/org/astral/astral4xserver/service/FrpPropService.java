package org.astral.astral4xserver.service;

import org.astral.astral4xserver.been.FrpProp;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FrpPropService {

    @Autowired
    private FrpPropRepository frpPropRepository;

    public List<FrpProp> getAllFrpProps() {
        return frpPropRepository.findAll();
    }

    public FrpProp saveFrpProp(FrpProp frpProp) {
        return frpPropRepository.save(frpProp);
    }
    public Optional<FrpProp> findByName(String name) {
        return frpPropRepository.findByName(name);
    }
    public List<FrpProp> findByUserId(int userId) {
        return frpPropRepository.findByUserId(userId);
    }
    // Other CRUD operations can be defined here
}
    