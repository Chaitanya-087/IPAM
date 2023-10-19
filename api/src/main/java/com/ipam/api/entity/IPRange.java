package com.ipam.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ipRanges")
@Getter
@Setter
public class IPRange extends NetworkObject {

  private String startAddress;
  private String endAddress;
}
