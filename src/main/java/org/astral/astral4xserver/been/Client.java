package org.astral.astral4xserver.been;

import java.time.LocalDateTime;

public class Client {
    private String key;
    private String api;
    private String mac;
    private LocalDateTime connect_time;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public LocalDateTime getConnect_time() {
        return connect_time;
    }

    public void setConnect_time(LocalDateTime connect_time) {
        this.connect_time = connect_time;
    }
}
