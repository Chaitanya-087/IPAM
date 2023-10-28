package com.ipam.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        @Test
    public void testIsValidIpWithValidIpAddress() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertTrue(networkUtil.isValidIp("192.168.1.1"));
    }

    @Test
    public void testIsValidIpWithInvalidIpAddress() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertFalse(networkUtil.isValidIp("invalid_address"));
    }

    @Test
    public void testIsValidCidrWithValidCidr() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertTrue(networkUtil.isValidCidr("192.168.1.0/24"));
    }

    @Test
    public void testIsValidCidrWithInvalidCidrFormat() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertFalse(networkUtil.isValidCidr("invalid_cidr"));
    }

    @Test
    public void testIsValidCidrWithInvalidIpAddress() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertFalse(networkUtil.isValidCidr("invalid_address/24"));
    }

    @Test
    public void testIsValidCidrWithInvalidMask() {
        NetworkUtil networkUtil = new NetworkUtil();
        assertFalse(networkUtil.isValidCidr("192.168.1.1/33"));
    }
}