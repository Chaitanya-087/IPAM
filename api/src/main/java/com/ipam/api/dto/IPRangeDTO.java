package com.ipam.api.dto;

import java.time.LocalDateTime;

import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;

import lombok.Data;

@Data
public class IPRangeDTO {
    private Long id;
    private String startAddress;
    private String endAddress;
    private Status status;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expirationDate;
    private User user;
}
