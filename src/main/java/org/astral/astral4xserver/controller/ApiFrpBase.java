package org.astral.astral4xserver.controller;

import com.google.gson.Gson;
import org.astral.astral4xserver.been.*;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.dao.RoleRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.astral.astral4xserver.service.FireWallService;
import org.astral.astral4xserver.service.FrpService;
import org.astral.astral4xserver.util.DailyKeyGenerator;
import org.astral.astral4xserver.util.OkHttp3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.astral.astral4xserver.Astral4xServerApplication.*;
import static org.astral.astral4xserver.Astral4xServerApplication.frp_host;
import static org.astral.astral4xserver.controller.ApiClientBase.authCacheService;

@CrossOrigin(origins = "https://4x.ink")
@RefreshScope
@RestController
@RequestMapping("/api/frpc")
public class ApiFrpBase {
    @Value("7000")
    private int bindport;
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
    @Autowired
    private FrpService frpService;
    @Autowired
    private FireWallService fireWallService;
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
    public FrpProp saveFrpProp(@RequestBody FrpProp frpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth) throws SocketException, NoSuchAlgorithmException {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return new FrpProp();
        }
        if(!frpProp.allisnotNull()) {
            return new FrpProp();
        }
        //限制remotePort
        if(frpProp.getRemotePort()<10000||frpProp.getRemotePort()>65535) {
            return new FrpProp();
        }
        Optional<FrpProp> ifcontainname = frpPropRepository.findByName(frpProp.getName());
        if (ifcontainname.isPresent()) {
            FrpProp frpProp1 = new FrpProp();
            frpProp1.setName("重复名称");
            return frpProp1;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {return null;}
        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        int id = currentUser.get().getId().intValue();
        if (!(frpProp.getUserId()==id)) {
            return null;
        }
        return frpPropRepository.save(frpProp);
    }
    @PutMapping("/frp")
    public ResponseEntity<FrpProp> updateFrpProp(@RequestBody FrpProp updatedFrpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        if(!updatedFrpProp.allisnotNull()) {
            return null;
        }
        if(updatedFrpProp.getRemotePort()<10000||updatedFrpProp.getRemotePort()>65535) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {return null;}

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        int id = currentUser.get().getId().intValue();
        if (!(updatedFrpProp.getUserId()==id)) {
            return null;
        }
        // 从数据库中检索要更新的 FrpProp 实体
        Optional<FrpProp> optionalFrpProp = frpPropRepository.findById(updatedFrpProp.getId());
        if (optionalFrpProp.isPresent()) {
            FrpProp frpProp = optionalFrpProp.get();
            if(!(frpProp.getName().equals(updatedFrpProp.getName()))) {
                if(frpPropRepository.findByName(updatedFrpProp.getName()).isPresent()) {
                    return null;
                }
            }
            // 更新 FrpProp 实体的属性
            frpProp.setName(frpProp.getName());
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {return null;}

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        int user_id = currentUser.get().getId().intValue();
        FrpProp frpProp = frpPropRepository.findById(id).get();
        if(frpProp.getUserId()!=user_id) {
            return null;
        }
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        frpPropRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/frpKey")
    public String getFrpKey(@RequestHeader(value = "X-Auth", required = true) String xAuth,@RequestBody Auth auth) throws SocketException, NoSuchAlgorithmException {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        if(auth.getX_auth().equals("yr3f7evsd98832rfy98uf397")) {
            return DailyKeyGenerator.generateDailyKey();
        }
        if(authCacheService.getData(auth.getToken()).getX_auth().equals(auth.getX_auth())) {
            return DailyKeyGenerator.generateDailyKey();
        }else {
            return "123456SHABI";
        }
    }
    @GetMapping("/frpRestart")
    public String frpRestart(@RequestHeader(value = "X-Auth", required = true) String xAuth,String password) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        if(password.equals("我求求你重启吧,我什么都愿意做的")) {
            new Thread(() -> {
                try {
                    frpService.stopFrps();
                    File frpsFile = new File(".//a4xs//frplinuxamd64//frps.json");
                    Gson gson = new Gson();
                    ServerConfig serverConfig = new ServerConfig();
                    serverConfig.setBindPort(7000);
                    if (frp_serverId == 2) {
                        OkHttp3 okHttp3 = new OkHttp3();
                        String head = okHttp3.sendRequest(host_web + ":" + port_web + "/api/safe/getAuth", "GET", null, null);
                        Map<String, String> map = new HashMap<>();
                        map.put("X-Auth", head);
                        Auth auth = new Auth();
                        auth.setX_auth("yr3f7evsd98832rfy98uf397");
                        String json = new Gson().toJson(auth);
                        String dailyKey = okHttp3.sendRequest(host_web + ":" + port_web + "/api/frpc/frpKey", "POST", map, json);
                        System.out.println(dailyKey);
                        serverConfig.setAuth(new ServerConfig.Auth("token", dailyKey));
                        serverConfig.setWebServer(new WebServerConfig(frp_host, 7500, "asdfghjkl", "asdfghjkl"));
                        String json2 = gson.toJson(serverConfig);
                        try {
                            PrintWriter pw = new PrintWriter(frpsFile);
                            pw.write(json2);
                            pw.close();
                        } catch (Exception e) {
                        }
                        frpService.startFrps();
                    } else {
                        String dailyKey = DailyKeyGenerator.generateDailyKey();
                        System.out.println(dailyKey);
                        serverConfig.setAuth(new ServerConfig.Auth("token", dailyKey));
                        serverConfig.setWebServer(new WebServerConfig(frp_host, 7500, "asdfghjkl", "asdfghjkl"));
                        String json = gson.toJson(serverConfig);
                        try {
                            PrintWriter pw = new PrintWriter(frpsFile);
                            pw.write(json);
                            pw.close();
                        } catch (Exception e) {
                        }
                        frpService.startFrps();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return "yes";
    }
}
