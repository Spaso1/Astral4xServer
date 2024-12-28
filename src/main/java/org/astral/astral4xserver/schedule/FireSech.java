package org.astral.astral4xserver.schedule;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Logger;
import org.astral.astral4xserver.been.FrpProp;
import org.astral.astral4xserver.been.FrpServerBoard;
import org.astral.astral4xserver.been.FrpServerBoards;
import org.astral.astral4xserver.dao.UserFrpUpdate;
import org.astral.astral4xserver.service.FireWallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.astral.astral4xserver.Astral4xServerApplication.frp_host;

@Component

public class FireSech {
    @Autowired
    private FireWallService fireWallService;
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(FireSech.class);
    @Scheduled(fixedRate = 3000)
    public void updatePort() {
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
            Gson gson = new Gson();
            FrpServerBoard frpServerBoard = gson.fromJson(res, FrpServerBoard.class);
            List<FrpServerBoards> proxies = frpServerBoard.getProxies();
            for (FrpServerBoards proxy : proxies) {
                String pro_name = proxy.getName();
                String status = proxy.getStatus();
                //logger.info("name: " + pro_name + " status: " + status);
                if(status.equals("online")) {
                    fireWallService.openPort(pro_name,proxy.getConf().getRemotePort());
                }
                if (status.equals("offline")) {
                    fireWallService.closePortByName(pro_name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // 添加打印堆栈跟踪以便调试
        } catch (Exception e) { // 捕获其他异常
            e.printStackTrace(); // 添加打印堆栈跟踪以便调试
        }
    }
}
