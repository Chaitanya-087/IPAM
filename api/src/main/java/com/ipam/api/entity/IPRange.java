package com.ipam.api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ipRanges")
@Getter
@Setter
public class IPRange extends NetworkObject {

  private String startAddress;
  private String endAddress;

  @OneToMany(cascade = CascadeType.ALL)
  private List<IPAddress> ipAddresses = new ArrayList<>();
}
