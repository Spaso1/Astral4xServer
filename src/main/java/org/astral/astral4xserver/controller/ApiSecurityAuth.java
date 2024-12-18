package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.message.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
@CrossOrigin(origins = "https://4x.ink")
@RequestMapping("/api/safe")
@RestController
public class ApiSecurityAuth {
    @Value("${astral4x.server.port}")
    private int port;
    @GetMapping("/getAuth")
    public static String getAuth() {
        // 获取当前时间并格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        String formattedDate = dateFormat.format(new Date());

        // 获取本地主机的IP地址
        String ipAddress = "";
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ipAddress = "unknown";
        }
        // 组合时间和IP地址
        String combinedInfo = formattedDate + ipAddress;
        // 生成密钥（使用SHA-256哈希算法）
        String key = hash(combinedInfo);
        return key;
    }
    @GetMapping("/getClientPort")
    public ApiResponse getClientPort() {
        return new ApiResponse(200, port+ "");
    }
    private static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
