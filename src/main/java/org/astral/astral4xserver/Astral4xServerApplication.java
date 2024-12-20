package org.astral.astral4xserver;

import com.google.gson.Gson;
import org.astral.astral4xserver.been.ServerConfig;
import org.astral.astral4xserver.been.WebServerConfig;
import org.astral.astral4xserver.service.FrpService;
import org.astral.astral4xserver.util.DailyKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class Astral4xServerApplication {
    private static FrpService frpService = new FrpService();
    public static void main(String[] args) throws SocketException, NoSuchAlgorithmException {
        new Thread(()->{
            try {
                launchFrps();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }).start();
        SpringApplication.run(Astral4xServerApplication.class, args);
    }
    public static void launchFrps() throws SocketException, NoSuchAlgorithmException {
        frpService.stopFrps();
        File frpsFile = new File(".//a4xs//frplinuxamd64//frps.json");
        Gson gson = new Gson();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setBindPort(7000);
        String dailyKey = DailyKeyGenerator.generateDailyKey();
        serverConfig.setAuth(new ServerConfig.Auth("token", dailyKey));
        serverConfig.setWebServer(new WebServerConfig("127.0.0.1", 7500, "asdfghjkl", "asdfghjkl"));
        String json = gson.toJson(serverConfig);
        try {
            PrintWriter pw = new PrintWriter(frpsFile);
            pw.write(json);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace(); // 添加异常处理
        }
        frpService.startFrpsWin();
        //frpService.startFrps();
    }
    @PreDestroy
    public void destroy() throws Exception {
        frpService.killAllProcesses();
    }
}
