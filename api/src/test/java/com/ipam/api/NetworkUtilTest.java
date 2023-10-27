package com.ipam.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ipam.api.util.NetworkUtil;

class NetworkUtilTest {

    @Test
    void testIpToLong() {
        NetworkUtil networkUtil = new NetworkUtil();

        String ipAddress = "192.168.1.1";
        long expectedLongValue = 3232235777L;

        long result = networkUtil.ipToLong(ipAddress);

        assertEquals(expectedLongValue, result);
    }

    @Test
    void testLongToIp() {
        NetworkUtil networkUtil = new NetworkUtil();

        long ip = 3232235777L;
        String expectedIpAddress = "192.168.1.1";

        String result = networkUtil.longToIp(ip);

        assertEquals(expectedIpAddress, result);
    }
}