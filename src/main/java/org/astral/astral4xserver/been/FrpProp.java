package org.astral.astral4xserver.been;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "frps" ,indexes = {
        @Index(name = "userId", columnList = "userId")
        ,@Index(name = "id", columnList = "id")

})
public class FrpProp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private int userId;
    @NotNull
    private String name;
    private String type;
    private String localIP;
    private int localPort;
    private int remotePort;
    private String remoteHost;
    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    public boolean allisnotNull() {
        return (this.name != null && this.type != null && this.localIP != null && this.localPort != 0 && this.remotePort != 0);
    }
}
