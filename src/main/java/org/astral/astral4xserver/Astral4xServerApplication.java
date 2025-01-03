package org.astral.astral4xserver;

import com.google.gson.Gson;
import org.astral.astral4xserver.been.Auth;
import org.astral.astral4xserver.been.ServerConfig;
import org.astral.astral4xserver.been.WebServerConfig;
import org.astral.astral4xserver.dao.FrpPropRepository;
import org.astral.astral4xserver.service.FireWallService;
import org.astral.astral4xserver.service.FrpPropService;
import org.astral.astral4xserver.service.FrpService;
import org.astral.astral4xserver.util.DailyKeyGenerator;
import org.astral.astral4xserver.util.OkHttp3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class Astral4xServerApplication {
    @Autowired
    private FireWallService fireWallService;
    private static FrpService frpService = new FrpService();
    @Autowired
    private FrpPropRepository frpPropService;
    @Value("${frp.serverId}")
    public static int frp_serverId;
    public static String frp_host = "127.0.0.1";
    public static String host_web = "http://121.41.121.48";
    public static int port_web = 8070;
    public static void main(String[] args) throws SocketException, NoSuchAlgorithmException {
        Map<String, String> map = new HashMap<>();
        for (int x = 0;x<args.length;x++) {
            String[] b = args[x].split("=");
            map.put(b[0],b[1]);
        }
        if(map.containsKey("frp_serverId")) {
            frp_serverId = Integer.parseInt(map.get("frp_serverId"));
        }else {
            frp_serverId=1;
        }

        new Thread(()->{
            try {
                Thread.sleep(10000);
                launchFrps();
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        SpringApplication.run(Astral4xServerApplication.class, args);
    }
    public static void launchFrps() throws IOException, NoSuchAlgorithmException {
        frpService.stopFrps();
        File frpsFile = new File(".//a4xs//frplinuxamd64//frps.json");
        Gson gson = new Gson();
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setBindPort(7000);
        if(frp_serverId==2) {
            System.out.println("作为从服务端启动");
            OkHttp3 okHttp3 = new OkHttp3();
            String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
            Map<String, String> map = new HashMap<>();
            map.put("X-Auth", head);
            Auth auth = new Auth();
            auth.setX_auth("yr3f7evsd98832rfy98uf397");
            String json = new Gson().toJson(auth);
            String dailyKey = okHttp3.sendRequest(host_web + ":"+port_web + "/api/frpc/frpKey", "POST", map, json);
            System.out.println(dailyKey);
            serverConfig.setAuth(new ServerConfig.Auth("token",dailyKey));
            serverConfig.setWebServer(new WebServerConfig(frp_host, 7500, "asdfghjkl", "asdfghjkl"));
            String json2 = gson.toJson(serverConfig);
            try {
                PrintWriter pw = new PrintWriter(frpsFile);
                pw.write(json2);
                pw.close();
            }catch (Exception e) {}
            frpService.startFrps();
        }else {
            System.out.println("frp_serverId:"+frp_serverId);
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
    }
    @PreDestroy
    public void destroy() throws Exception {
        frpPropService.updateStatusAll("下线");
        fireWallService.closeAll();
        frpService.killAllProcesses();
    }
}
