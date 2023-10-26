package com.ipam.api.util;

import org.springframework.stereotype.Component;

@Component
public class NetworkUtil {
    
    public long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressParts = ipAddress.split("\\.");
        for (int i = 0; i < 4; i++) {
          result = (result << 8) | Integer.parseInt(ipAddressParts[i]);
        }
        return result;
    }

    public String longToIp(long ip) {
        return String.format(
          "%d.%d.%d.%d",
          (ip >> 24) & 0xff,
          (ip >> 16) & 0xff,
          (ip >> 8) & 0xff,
          ip & 0xff
        );
    }
}
