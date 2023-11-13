package cn.mvp.global;

import java.util.ArrayList;
import java.util.List;

public class CfgInfo {
    private List<String> connectIps;

    public List<String> getConnectIps() {
        return connectIps;
    }

    public void setConnectIps(List<String> connectIps) {
        this.connectIps = connectIps;
    }

    public void addConnectIp(String ip) {
        if (this.connectIps == null) {
            this.connectIps = new ArrayList<>();
        }
        if (!this.connectIps.contains(ip)) {
            this.connectIps.add(ip);
        }
    }
}
