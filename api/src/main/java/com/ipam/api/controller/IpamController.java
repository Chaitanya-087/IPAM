package com.ipam.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ipam")
public class IpamController {
    
    @PostMapping("/ipaddress")
    public String createIpAddress() {
        return "createIpAddress";
    }

    @PostMapping("/reservation/{ipAddressId}")
    public String createReservation() {
        return "createReservation";
    }

    @PostMapping("/reservation/{ipRangeId}")
    public String createReservationRange() {
        return "createReservationRange";
    }

    @PostMapping("/iprange")
    public String createIpRange() {
        return "createIpRange";
    }

    
}
