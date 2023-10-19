package com.ipam.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  private String name;
  private String email;
  private Long ipAddressesCount;
  private Long ipRangesCount;
  private Long dnsRecordsCount;
  private Long subnetsCount;
}
