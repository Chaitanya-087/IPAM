package com.ipam.api.dto;

import java.time.LocalDateTime;

import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubnetDTO {
    private Long id;
    private User user;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiration;
    private String name;
    private String cidr;
    private String mask;
    private String gateway;
    private Long size;
}
