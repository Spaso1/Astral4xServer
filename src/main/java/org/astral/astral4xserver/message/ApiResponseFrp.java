package org.astral.astral4xserver.message;

import org.astral.astral4xserver.been.FrpProp;

import java.util.List;

public class ApiResponseFrp extends ApiResponse{
    private List<FrpProp> frp_list;
    public ApiResponseFrp(int status, String message, List<FrpProp> frp_list)
    {
        super(status, message);
        this.frp_list = frp_list;
    }

    public List<FrpProp> getFrp_list() {
        return frp_list;
    }

    public void setFrp_list(List<FrpProp> frp_list) {
        this.frp_list = frp_list;
    }
}
