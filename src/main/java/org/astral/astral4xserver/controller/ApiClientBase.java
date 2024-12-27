package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.been.*;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.dao.FrpServerRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.astral.astral4xserver.message.ApiResponse;
import org.astral.astral4xserver.message.AuthApiMessage;
import org.astral.astral4xserver.message.FrpMessage;
import org.astral.astral4xserver.message.FrpServerMessage;
import org.astral.astral4xserver.service.AuthCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "https://4x.ink")
@RefreshScope
@RequestMapping("/api/client")
@RestController
public class ApiClientBase {
    @Autowired
    public static AuthCacheService authCacheService = new AuthCacheService();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FrpPropRepository frpPropRepository;
    @Autowired
    private FrpServerRepository frpServerRepository;
    private Map<String, Long> config = new HashMap<>();

    @PostMapping("/auth")
    public Auth auth(@RequestBody AuthApiMessage authApiMessage, @RequestHeader(value = "X-Auth", required = true) String xAuth) {
        Auth auth = new Auth();
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return auth;
        }
        try {
            Optional<User> user = userRepository.findById(authApiMessage.getId());
            if (!user.isPresent()) {
                return new Auth();
            }
            if (!user.get().getToken().equals(authApiMessage.getToken())) {
                return new Auth();
            }
            if (!user.get().getUsername().equals(authApiMessage.getUsername())) return new Auth();
            if (authCacheService.getData(authApiMessage.getToken()) == null) {
                auth.setToken(authApiMessage.getToken());
                auth.setX_auth(AesUtils.encrypt(authApiMessage.getToken(), AesUtils.generateKey(128)));
                authCacheService.setData(authApiMessage.getToken(), auth);
            } else {
                auth = authCacheService.getData(authApiMessage.getToken());
            }
            config.put(auth.getX_auth(), user.get().getId());
            return auth;
        } catch (Exception e) {
            e.printStackTrace();
            return auth;
        }
    }

    @PutMapping("/frp")
    public ApiResponse updateAuthTime(@RequestBody Auth auth, @RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) return new ApiResponse(400, "error");
        Auth auth1 = authCacheService.getData(auth.getToken());
        if (auth1 == null) return new ApiResponse(400, "error");
        authCacheService.setData(auth.getToken(), auth1);
        return new ApiResponse(200, "success");
    }

    @PostMapping("/frp")
    public FrpMessage getFrp(@RequestBody Auth auth, @RequestHeader(value = "X-Auth", required = true) String xAuth) {
        FrpMessage frpMessage = new FrpMessage();
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            frpMessage.setCode(400);
            frpMessage.setStatus("error");
            frpMessage.setData(null);
            return frpMessage;
        }
        Auth auth1 = authCacheService.getData(auth.getToken());
        try {
            if (!auth1.getX_auth().equals(auth.getX_auth())) {
                frpMessage.setCode(400);
                frpMessage.setStatus("error");
                frpMessage.setData(null);
                return frpMessage;
            }
            long id = config.get(auth.getX_auth());
            List<FrpProp> frpPropList = frpPropRepository.findByUserId((int) id);
            frpMessage.setCode(200);
            frpMessage.setStatus("success");
            frpMessage.setData(frpPropList);
        } catch (Exception e) {
            frpMessage.setCode(400);
            frpMessage.setStatus("error");
            frpMessage.setData(null);
            return frpMessage;
        }
        return frpMessage;
    }

    @PostMapping("/frpSer")
    public FrpServerMessage getFrpServer(@RequestBody Auth auth, @RequestHeader(value = "X-Auth", required = true) String xAuth) {
        FrpServerMessage frpserverMessage = new FrpServerMessage();
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            frpserverMessage.setCode(400);
            return frpserverMessage;
        }
        Auth auth1 = authCacheService.getData(auth.getToken());
        try {
            if (!auth1.getX_auth().equals(auth.getX_auth())) {
                frpserverMessage.setCode(400);
                return frpserverMessage;
            }
            long id = config.get(auth.getX_auth());
            List<FrpServer> frpserver2 = frpServerRepository.findAll();
            frpserverMessage.setCode(200);
            ArrayList<FrpServer> data = new ArrayList<>();
            for (FrpServer frpServer : frpserver2) {
                data.add(frpServer);
            }
            frpserverMessage.setFrpServers(data);
            return frpserverMessage;
        } catch (Exception e) {
            frpserverMessage.setCode(400);
            return frpserverMessage;
        }}
}
