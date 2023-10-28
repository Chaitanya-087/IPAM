package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubnetForm {
    
    private String name;
    private String cidr;
    private String mask;
    private String gateway;
}
