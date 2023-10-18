package com.ipam.api.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private Long id;
    private String token;
    private String username;

    public JwtResponse(Long id,String accessToken,String username) {
        this.id = id;
        this.token = accessToken;
        this.username = username;
      }
}
