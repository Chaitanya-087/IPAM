package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatDTO {
    private long reservedCount;
    private long inuseCount;
    private long availableCount;
}
