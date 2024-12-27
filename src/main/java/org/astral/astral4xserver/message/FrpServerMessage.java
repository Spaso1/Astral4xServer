package org.astral.astral4xserver.message;

import org.astral.astral4xserver.been.FrpServer;

import java.util.ArrayList;

public class FrpServerMessage {
    private int code;
    private ArrayList<FrpServer> frpServers;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<FrpServer> getFrpServers() {
        return frpServers;
    }

    public void setFrpServers(ArrayList<FrpServer> frpServers) {
        this.frpServers = frpServers;
    }
}
