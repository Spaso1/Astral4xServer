package org.astral.astral4xserver.schedule;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.astral.astral4xserver.been.*;
import org.astral.astral4xserver.controller.ApiSecurityAuth;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.dao.UserFrpUpdate;
import org.astral.astral4xserver.service.FrpService;
import org.astral.astral4xserver.util.DailyKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.astral.astral4xserver.Astral4xServerApplication.frp_host;

@Component
public class FrpScheduler {
    @Autowired
    private FrpService frpService;
    @Autowired
    private FrpPropRepository frpPropRepository;
    private Map<String ,Long> streamMap = new java.util.HashMap<>();
    // 每天早上4点执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void restartFrps() throws SocketException, NoSuchAlgorithmException {
        frpService.stopFrps();
        File frpsFile = new File(".//a4xs//frplinuxamd64//frps.json");
        Gson gson = new Gson();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setBindPort(7000);
        String dailyKey = DailyKeyGenerator.generateDailyKey();
        serverConfig.setAuth(new ServerConfig.Auth("token",dailyKey));
        serverConfig.setWebServer(new WebServerConfig(frp_host, 7500, "asdfghjkl", "asdfghjkl"));
        String json = gson.toJson(serverConfig);
        try {
            PrintWriter pw = new PrintWriter(frpsFile);
            pw.write(json);
            pw.close();
        }catch (Exception e) {}
        frpService.startFrps();
    }
    //每5秒执行一次
    @Scheduled(fixedRate = 10000)
    public void updateCount() {
        OkHttpClient client = new OkHttpClient();
        String username = "asdfghjkl";
        String password = "asdfghjkl";
        // 创建 Basic Auth 字符串
        String credentials = username + ":" + password;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url("http://" + frp_host + ":7500/api/proxy/tcp")
                .header("Authorization", basicAuth)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            System.out.println(res);
            Gson gson = new Gson();
            FrpServerBoard frpServerBoard = gson.fromJson(res, FrpServerBoard.class);
            List<FrpServerBoards> proxies = frpServerBoard.getProxies();
            for (FrpServerBoards proxy : proxies) {
                String pro_name = proxy.getName();
                frpPropRepository.updateStreamByName(pro_name, proxy.getTodayTrafficOut());
                frpPropRepository.updateStreamTotalByName(pro_name, proxy.getTodayTrafficOut());
                if (!streamMap.containsKey(pro_name)) {
                    FrpProp frpProp = frpPropRepository.findByName(pro_name).orElse(null);
                    streamMap.put(pro_name, proxy.getTodayTrafficOut());
                    if (frpProp != null) {
                        UserFrpUpdate.queue.add(frpProp.getUserId() + ":" + proxy.getTodayTrafficOut());
                    }
                } else if (!(streamMap.get(pro_name) == proxy.getTodayTrafficOut())) {
                    FrpProp frpProp = frpPropRepository.findByName(pro_name).orElse(null);
                    if (frpProp != null) {
                        long change = proxy.getTodayTrafficOut() - streamMap.get(pro_name);
                        UserFrpUpdate.queue.add(frpProp.getUserId() + ":" + change);
                    }
                    streamMap.put(pro_name, proxy.getTodayTrafficOut());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // 添加打印堆栈跟踪以便调试
        } catch (Exception e) { // 捕获其他异常
            e.printStackTrace(); // 添加打印堆栈跟踪以便调试
        }
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
