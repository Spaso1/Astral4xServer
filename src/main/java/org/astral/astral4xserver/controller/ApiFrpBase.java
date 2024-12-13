package org.astral.astral4xserver.controller;

import com.sun.istack.NotNull;
import org.astral.astral4xserver.been.FrpProp;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.dao.RoleRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RefreshScope
@RestController
@RequestMapping("/api/frpc")
public class ApiFrpBase {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FrpPropRepository frpPropRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @GetMapping("/frp")
    public List<FrpProp> findByUseId(@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {}

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        int id = currentUser.get().getId().intValue();
        return frpPropRepository.findByUserId(id);
    }
    @PostMapping("/frp")
    public FrpProp saveFrpProp(@RequestBody FrpProp frpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {}

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        int id = currentUser.get().getId().intValue();
        frpProp.setUserId(id);
        return frpPropRepository.save(frpProp);
    }
    @PutMapping("/frp")
    public ResponseEntity<FrpProp> updateFrpProp(@RequestBody FrpProp updatedFrpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        // 从数据库中检索要更新的 FrpProp 实体
        Optional<FrpProp> optionalFrpProp = frpPropRepository.findById(updatedFrpProp.getId());
        if (optionalFrpProp.isPresent()) {
            FrpProp frpProp = optionalFrpProp.get();

            // 更新 FrpProp 实体的属性
            frpProp.setName(updatedFrpProp.getName());
            frpProp.setType(updatedFrpProp.getType());
            frpProp.setLocalIP(updatedFrpProp.getLocalIP());
            frpProp.setLocalPort(updatedFrpProp.getLocalPort());
            frpProp.setRemotePort(updatedFrpProp.getRemotePort());

            // 保存更新后的 FrpProp 实体
            FrpProp savedFrpProp = frpPropRepository.save(frpProp);
            return ResponseEntity.ok(savedFrpProp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/frp/{id}")
    public ResponseEntity<Void> deleteFrpProp(@PathVariable Long id,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        frpPropRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
