package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginBody {
    private String username;
    private String password;
}
