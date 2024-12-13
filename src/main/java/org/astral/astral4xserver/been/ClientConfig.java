package org.astral.astral4xserver.been;

import java.util.List;

public class ClientConfig {
    private String serverAddr;
    private int serverPort;
    private Auth auth;
    private List<Proxy> proxies;

    // Default constructor
    public ClientConfig() {
    }

    // Parameterized constructor
    public ClientConfig(String serverAddr, int serverPort, Auth auth, List<Proxy> proxies) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.auth = auth;
        this.proxies = proxies;
    }

    // Getter and Setter for serverAddr
    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    // Getter and Setter for serverPort
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    // Getter and Setter for auth
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    // Getter and Setter for proxies
    public List<Proxy> getProxies() {
        return proxies;
    }

    public void setProxies(List<Proxy> proxies) {
        this.proxies = proxies;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "serverAddr='" + serverAddr + '\'' +
                ", serverPort=" + serverPort +
                ", auth=" + auth +
                ", proxies=" + proxies +
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

    // Inner class for Proxy
    public static class Proxy {
        private String name;
        private String type;
        private String localIP;
        private int localPort;
        private int remotePort;

        // Default constructor
        public Proxy() {
        }

        // Parameterized constructor
        public Proxy(String name, String type, String localIP, int localPort, int remotePort) {
            this.name = name;
            this.type = type;
            this.localIP = localIP;
            this.localPort = localPort;
            this.remotePort = remotePort;
        }

        // Getter and Setter for name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // Getter and Setter for type
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        // Getter and Setter for localIP
        public String getLocalIP() {
            return localIP;
        }

        public void setLocalIP(String localIP) {
            this.localIP = localIP;
        }

        // Getter and Setter for localPort
        public int getLocalPort() {
            return localPort;
        }

        public void setLocalPort(int localPort) {
            this.localPort = localPort;
        }

        // Getter and Setter for remotePort
        public int getRemotePort() {
            return remotePort;
        }

        public void setRemotePort(int remotePort) {
            this.remotePort = remotePort;
        }

        @Override
        public String toString() {
            return "Proxy{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", localIP='" + localIP + '\'' +
                    ", localPort=" + localPort +
                    ", remotePort=" + remotePort +
                    '}';
        }
    }
}
