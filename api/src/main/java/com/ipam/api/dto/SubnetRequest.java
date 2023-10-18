package com.ipam.api.dto;

import lombok.Data;

@Data
public class SubnetRequest {
    
    private String name;
    private String cidr;
    private String mask;
    private String gateway;
}
