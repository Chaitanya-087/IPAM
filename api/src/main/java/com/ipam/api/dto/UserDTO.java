package com.ipam.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

  private Long id;
  private String name;
  private String email;
  private Long ipAddressesCount;
  private Long ipRangesCount;
  private Long subnetsCount;
}
