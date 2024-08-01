package cn.mvp.global;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.mlibs.utils.ListUtils;

public class CfgInfo {
    private List<String> connectIps;
    private String lastConnIp;

    public List<String> getConnectIps() {
        return connectIps;
    }

    public String[] getConnectIpsArr() {
        if (connectIps != null) {
            return ListUtils.toArray(connectIps, new String[0]);
        }
        return null;
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

    public String getLastConnIp() {
        return lastConnIp;
    }

    public void setLastConnIp(String lastConnIp) {
        this.lastConnIp = lastConnIp;
    }
}
