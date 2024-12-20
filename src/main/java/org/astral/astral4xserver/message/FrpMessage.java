package org.astral.astral4xserver.message;

import org.astral.astral4xserver.been.FrpProp;

import java.util.List;

public class FrpMessage {
    private int code;
    private String status;
    private List<FrpProp> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FrpProp> getData() {
        return data;
    }

    public void setData(List<FrpProp> data) {
        this.data = data;
    }
}
