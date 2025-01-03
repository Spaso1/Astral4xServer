package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.been.FrpProp;
import org.astral.astral4xserver.been.Role;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.astral.astral4xserver.message.ApiResponse;
import org.astral.astral4xserver.message.ApiResponseFrp;
import org.astral.astral4xserver.service.FireWallService;
import org.astral.astral4xserver.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/")
public class ApiAdminBase {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FrpPropRepository frpPropRepository;
    @Autowired
    private FireWallService fireWallService;
    @Autowired
    private MailService mailService;
    public static Map<String,String> adminmap = new HashMap<>();
    @GetMapping("/frpProxyInfo")
    public ApiResponseFrp start(@RequestHeader(value = "X-Auth", required = true) String xAuth){
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ApiResponseFrp(401, "未认证",null);
        }

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        Role role = currentUser.get().getRoles().iterator().next();
        if (!role.getName().equals("管理员")) {
            return new ApiResponseFrp(401, "未认证",null);
        }
        List<FrpProp> list= frpPropRepository.getAll();
        return new ApiResponseFrp(200, "success",list);
    }
    @PostMapping("/frpProxyInfo")//change
    public ApiResponse updateFrpProp(@RequestBody FrpProp frpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth){
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {return new ApiResponse(401, "未认证");}
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ApiResponseFrp(401, "未认证",null);
        }

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        Role role = currentUser.get().getRoles().iterator().next();
        if (!role.getName().equals("管理员")) {
            return new ApiResponseFrp(401, "未认证",null);
        }
        frpPropRepository.updateById(frpProp);
        return new ApiResponse(200, "success");
    }
    @GetMapping("/usedFrp")
    public ApiResponseFrp usedFrp(@RequestHeader(value = "X-Auth", required = true) String xAuth){
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ApiResponseFrp(401, "未认证",null);
        }
        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        Role role = currentUser.get().getRoles().iterator().next();
        if (!role.getName().equals("管理员")){
            return new ApiResponseFrp(401, "未认证",null);
        }
        List<FrpProp> list= frpPropRepository.findFrpPropsByStatus("上线");
        return new ApiResponseFrp(200, "success",list);
    }
    @PostMapping("/closeFrp")
    public ApiResponse closeFrp(@RequestParam FrpProp frpProp,@RequestHeader(value = "X-Auth", required = true) String xAuth,String reason){
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ApiResponse(401, "未认证");
        }
        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);
        Role role = currentUser.get().getRoles().iterator().next();
        if (!role.getName().equals("管理员")){
            return new ApiResponse(401, "未认证");
        }
        fireWallService.closePortByName(frpProp.getName());
        new Thread(()->{mailService.sendSimpleMail("astralpath@163.com",userRepository.getById((long) frpProp.getUserId()).getEmail(),"astralpath@163.com","下线通知","下线通知:" + frpProp.getName() + "\n原因:" + reason);});
        return new ApiResponse(200, "success");
    }
}
