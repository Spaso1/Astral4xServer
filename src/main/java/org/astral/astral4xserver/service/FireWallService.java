package org.astral.astral4xserver.service;

import org.astral.astral4xserver.util.FireWall;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FireWallService {
    private Map<String,Integer> ports = new HashMap<>();
    public void openPort(String name,int port) {
        if(!ports.containsKey(name)) {
            ports.put(name,port);
            FireWall.openPort(port);
        }
    }
    public void closePortByName(String name) {
        if(ports.containsKey(name)) {
            FireWall.closePort(ports.get(name));
            ports.remove(name);
        }
    }
    public Map<String, Integer> getPorts() {
        return ports;
    }
    public void closeAll() {
        for (Integer port : ports.values()) {
            System.out.println("close port:"+port);
            FireWall.closePort(port);
        }
    }
}
