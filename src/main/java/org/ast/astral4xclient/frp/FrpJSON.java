package org.ast.astral4xclient.frp;

import org.ast.astral4xclient.been.Auth;

import java.util.ArrayList;
import java.util.List;

public class FrpJSON {
    private String serverAddr;
    private int serverPort;
    private AuthIn auth;
    private List<proxy> proxies;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public AuthIn getAuth() {
        return auth;
    }

    public void setAuth(AuthIn auth) {
        this.auth = auth;
    }

    public List<proxy> getProxies() {
        return proxies;
    }

    public void setProxies(List<proxy> proxies) {
        this.proxies = proxies;
    }
}
