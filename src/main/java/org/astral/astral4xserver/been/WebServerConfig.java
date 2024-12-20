package org.astral.astral4xserver.been;

public class WebServerConfig {
    private String addr;
    private int port;
    private String user;
    private String password;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public WebServerConfig(String addr, int port, String user, String password) {
        this.addr = addr;
        this.port = port;
        this.user = user;
        this.password = password;
    }
}
