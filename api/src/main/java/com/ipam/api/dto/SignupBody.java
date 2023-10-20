package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupBody {

    private String username;
    private String email;
    private String password;
}
