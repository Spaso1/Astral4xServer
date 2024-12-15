package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.been.AesUtils;
import org.astral.astral4xserver.been.Role;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.dao.RoleRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.astral.astral4xserver.message.ApiResponse;
import org.astral.astral4xserver.message.LoginRequest;
import org.astral.astral4xserver.service.DataCacheService;
import org.astral.astral4xserver.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
@CrossOrigin
@RefreshScope
@RequestMapping("/api/users")
@RestController
public class ApiUserBase {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataCacheService datacacheService;
    @Autowired
    private MailService mailService;
    //注册
    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody User user,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return new ApiResponse(400, "Invalid x-auth");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //获得一个LocalDateTime对象
        user.setCreated_at(LocalDateTime.now());
        // 获取默认角色（例如 ROLE_USER）
        Set<Role> roles = new HashSet<>();
        Optional<Role> roleOptional = roleRepository.findByName("用户");
        roleOptional.ifPresent(roles::add);
        user.setRoles(roles);
        String email = user.getEmail();
        String url = Base64.getEncoder().encodeToString(email.getBytes());
        //url只取前6位
        url = url.substring(0, 6);
        datacacheService.setData(url, user);
        ApiResponse apiResponse = new ApiResponse(200, "User registered successfully , please auth your email!");
        String finalUrl = url;
        new Thread(()->{
            mailService.sendSimpleMail("astralpath@163.com", email, "", "欢迎注册，请访问下面链接激活账户：http://localhost:8080/api/users/auth/" + finalUrl, "");
        });
        System.out.println("http://localhost:8080/api/users/auth/" + url);
        return apiResponse;
    }
    @GetMapping("/auth/{url}")
    public ApiResponse authUser(@PathVariable String url) throws Exception {
        User user = datacacheService.getData(url);
        if (user == null) {
            return new ApiResponse(400, "User not found");
        }
        user.setToken(AesUtils.encrypt(user.getUsername(), AesUtils.generateKey(128)));
        userRepository.save(user);
        return new ApiResponse(200, "User activated successfully");
    }
    @PostMapping("/loginUser")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("username", authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/info")
    public ResponseEntity<User> getUserInfo(@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // 返回未认证状态
        }

        String currentUserName = authentication.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUserName);

        if (currentUser.isPresent()) {
            return ResponseEntity.ok(currentUser.get());
        } else {
            return ResponseEntity.status(404).build(); // 返回用户未找到状态
        }
    }

    /**
     *     @DeleteMapping("/{id}")
     *     public void deleteUser(@PathVariable long id) {
     *         userRepository.deleteById(id);
     *     }
     */

    /**
     * @PutMapping
     * @param id
     * @param updatedUser
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // 返回未认证状态
        }
        User currentUser = (User) authentication.getPrincipal();
        if(!(currentUser.getId()==id)) {
            return ResponseEntity.status(403).build();
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 更新用户名
            if (updatedUser.getUsername() != null) {
                user.setUsername(updatedUser.getUsername());
            }

            // 更新密码
            if (updatedUser.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

               // 保存更新后的用户
            return ResponseEntity.ok(userRepository.save(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
