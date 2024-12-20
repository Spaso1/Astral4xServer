package org.astral.astral4xserver.been;

import org.springframework.boot.web.server.WebServer;

public class ServerConfig {
    private int bindPort;
    private Auth auth;
    private WebServerConfig webServer;
    // Default constructor
    public ServerConfig() {
    }

    // Parameterized constructor
    public ServerConfig(int bindPort, Auth auth) {
        this.bindPort = bindPort;
        this.auth = auth;
    }

    // Getter and Setter for bindPort
    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    // Getter and Setter for auth
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public WebServerConfig getWebServer() {
        return webServer;
    }

    public void setWebServer(WebServerConfig webServer) {
        this.webServer = webServer;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "bindPort=" + bindPort +
                ", auth=" + auth +
                '}';
    }

    // Inner class for Auth
    public static class Auth {
        private String method;
        private String token;

        // Default constructor
        public Auth() {
        }

        // Parameterized constructor
        public Auth(String method, String token) {
            this.method = method;
            this.token = token;
        }

        // Getter and Setter for method
        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        // Getter and Setter for token
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "Auth{" +
                    "method='" + method + '\'' +
                    ", token='" + token + '\'' +
                    '}';
        }
    }
}
