package org.astral.astral4xserver.been;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "frpservers")
public class FrpServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(name = "ip")
    private String ip;
    @NotNull
    @Column(name = "port")
    private String port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
