package org.astral.astral4xserver.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        // 创建 BCryptPasswordEncoder 实例
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // 原始密码
        String[] rawPasswords = {"password1", "password2", "password3"};

        for (String rawPassword : rawPasswords) {
            // 生成加密后的密码
            String encodedPassword = encoder.encode(rawPassword);
            System.out.println("原始密码: " + rawPassword);
            System.out.println("加密后的密码: " + encodedPassword);
        }
    }
}
