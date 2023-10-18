package com.ipam.api.dto;

import lombok.Data;

@Data
public class UserDTO {

  private Long id;
  private String name;
  private String email;
  private Long ipAddressesCount;
  private Long ipRangesCount;
  private Long dnsRecordsCount;
  private Long subnetsCount;
}
