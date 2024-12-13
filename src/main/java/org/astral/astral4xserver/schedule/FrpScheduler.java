package org.astral.astral4xserver.schedule;

import com.google.gson.Gson;
import org.astral.astral4xserver.been.ServerConfig;
import org.astral.astral4xserver.controller.ApiSecurityAuth;
import org.astral.astral4xserver.service.FrpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class FrpScheduler {
    @Autowired
    private FrpService frpService;
    // 每天早上4点执行
    @Scheduled(cron = "0 0 4 * * ?")
    public void restartFrps() {
        frpService.stopFrps();
        File frpsFile = new File(".//a4xs//frplinuxamd64//frps.json");
        Gson gson = new Gson();
        String key = getAuth();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setBindPort(7000);
        serverConfig.setAuth(new ServerConfig.Auth("token",key));
        String json = gson.toJson(serverConfig);
        try {
            PrintWriter pw = new PrintWriter(frpsFile);
            pw.write(json);
            pw.close();
        }catch (Exception e) {}
        frpService.startFrps();
    }

    public static String getAuth() {
        // 获取当前时间并格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
