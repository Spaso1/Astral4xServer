package org.ast.astral4xclient.sech;

import com.google.gson.Gson;
import org.ast.astral4xclient.been.Auth;
import org.ast.astral4xclient.been.User;
import org.ast.astral4xclient.controller.ApiClient;
import org.ast.astral4xclient.util.OkHttp3;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.ast.astral4xclient.Astral4xClientApplication.host_web;
import static org.ast.astral4xclient.Astral4xClientApplication.port_web;
import static org.ast.astral4xclient.controller.ApiClient.auth;
import static org.ast.astral4xclient.service.FrpService.killPlainProcess;

@Component
public class SecMaker {
    @Scheduled(cron = "0 0 4 * * ?")
    public void restartFrps() throws Exception {
        killPlainProcess();
        ApiClient.launch();
    }
    @Scheduled(fixedRate = 60000)
    public void updateAuth() throws IOException {
        if(!(auth==null)) {
            OkHttp3 okHttp3 = new OkHttp3();
            Gson gson = new Gson();
            String json = gson.toJson(auth);
            String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
            Map<String, String> map = new HashMap<>();
            map.put("X-Auth", head);
            String postResponse = okHttp3.sendRequest(host_web + ":"+port_web +"/api/client/frp", "PUT", map, json);
            if(postResponse.contains("200")) {
                System.out.println("更新令牌完成");
            }
        }
    }
    @Scheduled(fixedRate = 10000)
    public void findCount() throws Exception {
        if(!(auth==null)) {
            OkHttp3 okHttp3 = new OkHttp3();
            Auth auth = ApiClient.auth;
            Gson gson = new Gson();
            String json = gson.toJson(auth);
            String head = okHttp3.sendRequest(host_web + ":"+port_web +"/api/safe/getAuth", "GET", null,null);
            Map<String, String> map = new HashMap<>();
            map.put("X-Auth", head);
            String postResponse = okHttp3.sendRequest(host_web + ":"+port_web +"/api/users/findByAuth", "POST", map, json);
            System.out.println(postResponse);
            User user = gson.fromJson(postResponse, User.class);
            if(user.getCountStream()>0) {
                System.out.println("剩余" + user.getCountStream() / 1024 /1024 + " MB");
            }else {
                killPlainProcess();
                System.out.println("流量已用完");
            }
        }
    }
}
