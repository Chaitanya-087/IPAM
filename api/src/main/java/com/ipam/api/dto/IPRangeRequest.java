package com.ipam.api.dto;

import lombok.Data;

@Data
public class IPRangeRequest {
    private String startAddress;
    private String endAddress;
}
