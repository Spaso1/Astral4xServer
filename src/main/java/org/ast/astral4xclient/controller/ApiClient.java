package org.ast.astral4xclient.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ast.astral4xclient.been.ApiMessage;
import org.ast.astral4xclient.been.Auth;
import org.ast.astral4xclient.been.FrpProp;
import org.ast.astral4xclient.been.FrpServer;
import org.ast.astral4xclient.frp.AuthIn;
import org.ast.astral4xclient.frp.FrpJSON;
import org.ast.astral4xclient.frp.proxy;
import org.ast.astral4xclient.message.AuthApiMessage;
import org.ast.astral4xclient.message.FrpMessage;
import org.ast.astral4xclient.service.FrpService;
import org.ast.astral4xclient.util.OkHttp3;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ast.astral4xclient.Astral4xClientApplication.host_web;
import static org.ast.astral4xclient.Astral4xClientApplication.port_web;
import static org.ast.astral4xclient.service.FrpService.killPlainProcess;

@RestController
@RequestMapping("/api/client")
public class ApiClient {
    public static Thread thread;
    public static FrpService service;
    public static FrpMessage frpMessage;
    public static FrpJSON frpJSON;
    public static Auth auth;
    @PostMapping("/connect")
    public ApiMessage connect(@RequestBody Auth auth) throws IOException {
        Gson gson = new Gson();
        OkHttp3 okHttp3 = new OkHttp3();
        String json = gson.toJson(auth);
        String head = okHttp3.sendRequest( host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
        Map<String, String> map = new HashMap<>();
        map.put("X-Auth", head);
        String postResponse = okHttp3.sendRequest(host_web + ":"+port_web + "/api/client/frp", "POST", map, json);
        FrpMessage frpMessage = gson.fromJson(postResponse, FrpMessage.class);
        this.frpMessage = frpMessage;
        System.out.println(postResponse);
        if(postResponse.contains("400")) {
            return new ApiMessage(1, "auth错误");
        }
        ApiClient.auth = auth;
        return new ApiMessage(0, "ok");
    }
    @GetMapping("/launch")
    public ApiMessage launch() throws IOException {
        ApiMessage apiMessage = new ApiMessage(0, "ok");
        OkHttp3 okHttp3 = new OkHttp3();
        String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
        Map<String, String> map = new HashMap<>();
        map.put("X-Auth", head);
        String key = okHttp3.sendRequest(host_web + ":"+port_web + "/api/frpc/frpKey", "GET", map, null);
        frpJSON = new FrpJSON();
        frpJSON.setServerAddr(returnHost().split(",")[0]);
        frpJSON.setServerPort(Integer.parseInt(returnHost().split(":")[1]));
        AuthIn authIn = new AuthIn();
        authIn.setMethod("token");
        authIn.setToken(key);
        //authIn.setToken("a4dca34b49f34ff60069cb451b4fd6fda25e2d1c9b7073de8697af3c9a2d1b29");
        frpJSON.setAuth(authIn);
        List<FrpProp> data = frpMessage.getData();
        List<proxy> proxies = new ArrayList<>();
        for (FrpProp frpProp : data) {
            proxy proxy = new proxy();
            proxy.setName(frpProp.getName());
            proxy.setType(frpProp.getType());
            proxy.setLocalIP(frpProp.getLocalIP());
            proxy.setLocalPort(frpProp.getLocalPort());
            proxy.setRemotePort(frpProp.getRemotePort());
            proxies.add(proxy);
        }
        frpJSON.setProxies(proxies);
        if(service==null) {
            service = new FrpService();
            thread = new Thread(service);
            thread.start();
        }else {

        }
        return apiMessage;
    }
    @GetMapping("/status")
    public ApiMessage status() throws IOException {
        ApiMessage apiMessage = new ApiMessage(0, "ok");
        apiMessage.setMessage("{Auth:" + auth.getToken() + ",Status:" + FrpService.status + "}");
        return apiMessage;
    }
    @GetMapping("/update")
    public ApiMessage update() throws Exception {
        killPlainProcess();
        {
            Gson gson = new Gson();
            OkHttp3 okHttp3 = new OkHttp3();
            String json = gson.toJson(auth);
            String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
            Map<String, String> map = new HashMap<>();
            map.put("X-Auth", head);
            String postResponse = okHttp3.sendRequest(host_web + ":"+port_web + "/api/client/frp", "POST", map, json);
            FrpMessage frpMessage = gson.fromJson(postResponse, FrpMessage.class);
            this.frpMessage = frpMessage;
            System.out.println(postResponse);
            if(postResponse.contains("400")) {
                return new ApiMessage(1, "auth错误");
            }
        }
        System.out.println("更新令牌");
        ApiMessage apiMessage = new ApiMessage(0, "ok");
        OkHttp3 okHttp3 = new OkHttp3();
        String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
        Map<String, String> map = new HashMap<>();
        map.put("X-Auth", head);
        String key = okHttp3.sendRequest(host_web + ":"+port_web + "/api/frpc/frpKey", "GET", map, null);
        frpJSON = new FrpJSON();
        frpJSON.setServerAddr(returnHost().split(",")[0]);
        frpJSON.setServerPort(Integer.parseInt(returnHost().split(":")[1]));
        AuthIn authIn = new AuthIn();
        authIn.setMethod("token");
        authIn.setToken(key);
        //authIn.setToken("a4dca34b49f34ff60069cb451b4fd6fda25e2d1c9b7073de8697af3c9a2d1b29");
        frpJSON.setAuth(authIn);
        List<FrpProp> data = frpMessage.getData();
        List<proxy> proxies = new ArrayList<>();
        for (FrpProp frpProp : data) {
            proxy proxy = new proxy();
            proxy.setName(frpProp.getName());
            proxy.setType(frpProp.getType());
            proxy.setLocalIP(frpProp.getLocalIP());
            proxy.setLocalPort(frpProp.getLocalPort());
            proxy.setRemotePort(frpProp.getRemotePort());
            proxies.add(proxy);
        }
        frpJSON.setProxies(proxies);
        service = new FrpService();
        thread = new Thread(service);
        thread.start();
        return apiMessage;
    }
    public static String returnHost() throws IOException {
        OkHttp3 okHttp3 = new OkHttp3();
        String head = okHttp3.sendRequest(host_web + ":"+port_web + "/api/safe/getAuth", "GET", null, null);
        Map<String, String> map = new HashMap<>();
        map.put("X-Auth", head);
        String postResponse = okHttp3.sendRequest(host_web + ":"+port_web + "/api/client/frpSer", "GET", map, new Gson().toJson(auth));
        List<FrpServer> frpServers = new Gson().fromJson(postResponse, new TypeToken<List<FrpServer>>(){}.getType());
        return frpServers.get(0).getIp() + "," + frpServers.get(0).getPort();
    }
}
