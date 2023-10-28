package com.ipam.api.util;

import java.util.regex.Pattern;
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

  public boolean isValidIp(String address) {
    String regex =
      "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
    Pattern p = Pattern.compile(regex);
    return p.matcher(address).matches();
  }

  public boolean isValidCidr(String cidr) {
    String[] parts = cidr.split("/");
    if (parts.length != 2) {
      return false;
    }
    String ip = parts[0];
    String mask = parts[1];
    if (!isValidIp(ip)) {
      return false;
    }
    int maskInt = Integer.parseInt(mask);
    if (maskInt < 0 || maskInt > 32) {
      return false;
    }
    return true;
  }
}
