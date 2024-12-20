package org.astral.astral4xserver.schedule;

import com.google.gson.Gson;
import org.astral.astral4xserver.been.ServerConfig;
import org.astral.astral4xserver.been.WebServerConfig;
import org.astral.astral4xserver.service.FrpService;
import org.astral.astral4xserver.util.DailyKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

@Component
@ComponentScan
public class Companent implements CommandLineRunner {
    @Autowired
    private FrpService frpService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Launch");
        launchFrps();
    }

    public void launchFrps() throws SocketException, NoSuchAlgorithmException {
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
}
