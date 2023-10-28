package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPRangeForm {
    
    private String startAddress;
    private String endAddress;
}
