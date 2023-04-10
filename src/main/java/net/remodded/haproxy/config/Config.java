package net.remodded.haproxy.config;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
public class Config {

    @SerializedName("enable-proxy-protocol")
    public boolean enableProxyProtocol = true;

    @SerializedName("proxy-protocol-whitelisted-ips")
    public List<String> whitelistedIPs = new ArrayList<>();

    @SerializedName("whitelistTCPShieldServers")
    public boolean whitelistTCPShieldServers = false;
}
