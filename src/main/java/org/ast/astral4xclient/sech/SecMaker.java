package org.ast.astral4xclient.sech;

import com.google.gson.Gson;
import org.ast.astral4xclient.controller.ApiClient;
import org.ast.astral4xclient.util.OkHttp3;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.ast.astral4xclient.controller.ApiClient.auth;

@Component
public class SecMaker {
    @Scheduled(fixedRate = 60000)
    public void updateAuth() throws IOException {
        if(!(auth==null)) {
            OkHttp3 okHttp3 = new OkHttp3();
            Gson gson = new Gson();
            String json = gson.toJson(auth);
            String head = okHttp3.sendRequest("http://127.0.0.1:8070/api/safe/getAuth", "GET", null, null);
            Map<String, String> map = new HashMap<>();
            map.put("X-Auth", head);
            String postResponse = okHttp3.sendRequest("http://127.0.0.1:8070/api/client/frp", "PUT", map, json);
            System.out.println("更新完成");
        }
    }
}
