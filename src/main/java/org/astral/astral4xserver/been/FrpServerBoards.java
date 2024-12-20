package org.astral.astral4xserver.been;

public class FrpServerBoards {
    private String name;
    private Conf conf;
    private String clientVersion;
    private long todayTrafficIn;
    private long todayTrafficOut;
    private int curConns;
    private String lastStartTime;
    private String lastCloseTime;
    private String status;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public long getTodayTrafficIn() {
        return todayTrafficIn;
    }

    public void setTodayTrafficIn(long todayTrafficIn) {
        this.todayTrafficIn = todayTrafficIn;
    }

    public long getTodayTrafficOut() {
        return todayTrafficOut;
    }

    public void setTodayTrafficOut(long todayTrafficOut) {
        this.todayTrafficOut = todayTrafficOut;
    }

    public int getCurConns() {
        return curConns;
    }

    public void setCurConns(int curConns) {
        this.curConns = curConns;
    }

    public String getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(String lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public String getLastCloseTime() {
        return lastCloseTime;
    }

    public void setLastCloseTime(String lastCloseTime) {
        this.lastCloseTime = lastCloseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

