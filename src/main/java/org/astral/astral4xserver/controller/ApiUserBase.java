package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.been.AesUtils;
import org.astral.astral4xserver.been.Auth;
import org.astral.astral4xserver.been.Role;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.dao.RoleRepository;
import org.astral.astral4xserver.dao.UserRepository;
import org.astral.astral4xserver.message.ApiResponse;
import org.astral.astral4xserver.message.LoginRequest;
import org.astral.astral4xserver.service.DataCacheService;
import org.astral.astral4xserver.service.MailService;
import org.astral.astral4xserver.service.StringCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.astral.astral4xserver.controller.ApiClientBase.authCacheService;


@CrossOrigin(origins = "https://4x.ink")
@RefreshScope
@RequestMapping("/api/users")
@RestController
public class ApiUserBase {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserBase.class);
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
    @Autowired
    private StringCacheService stringCacheService;
    @Value("${spring.to.host}")
    private String host;
    //注册
    @PostMapping("/register")
    public ApiResponse registerUser(@RequestBody User user,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if(!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return new ApiResponse(400, "Invalid x-auth");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            return new ApiResponse(400, "Username is already taken!");
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
            mailService.sendSimpleMail("astralpath@163.com", email, "astralpath@163.com", "欢迎注册，请访问下面链接激活账户：http://" + host+ "/api/users/auth/" + finalUrl, "http://" + host+ "/api/users/auth/" + finalUrl);
        }).start();
        logger.info("http://" + host+ "/api/users/auth/" + url);
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
        return currentUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
    }
    @PostMapping("/findByAuth")
    public ResponseEntity<User> findByAuth(@RequestBody Auth auth, @RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) return null;
        Auth auth1 = authCacheService.getData(auth.getToken());
        if(auth1==null) return null;
        Optional<User> user = userRepository.findByToken(auth1.getToken());
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
    }
    /**
     *     @DeleteMapping("/{id}")
     *     public void deleteUser(@PathVariable long id) {
     *         userRepository.deleteById(id);
     *     }
     */

    /**
     * @PutMapping
     * @param updatedUser
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // 返回未认证状态
        }
        try {
            User currentUser = (User) authentication.getPrincipal();
            if(!(currentUser.getId()== updatedUser.getId())) {
                return ResponseEntity.status(403).build();
            }
        }catch (Exception e) {
            return null;
        }

        Optional<User> optionalUser = userRepository.findById(updatedUser.getId());
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
    @PostMapping("/updateNum")
    public ApiResponse sendEmail(@RequestParam String email,@RequestParam String password,@RequestHeader(value = "X-Auth", required = true) String xAuth) {
        if (!xAuth.equals(ApiSecurityAuth.getAuth())) {
            return null;
        }
        new Thread(() -> {
            try {
                String url = AesUtils.encrypt(email + new Random(1000), AesUtils.generateKey(128));
                url = url.substring(0, 6);
                stringCacheService.setData(email,url);
                mailService.sendSimpleMail("astralpath@163.com", email, "astralpath@163.com", "密码更改", "http://" + host + "/api/users/updateNum?"+ "email=" + email + "&email_auth=" + url + "&password=" + password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        return new ApiResponse(200, "已发送邮件");
    }
    @GetMapping("/updateNum")
    public ResponseEntity<User> updateUpdateNum(@RequestParam String email,@RequestParam String email_auth,@RequestParam String password) {
        if (!stringCacheService.getData(email).equals(email_auth)) {
            return ResponseEntity.status(400).build();
        }
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 更新密码
            if (password != null) {
                user.setPassword(passwordEncoder.encode(password));
            }

            // 保存更新后的用户
            return ResponseEntity.ok(userRepository.save(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
