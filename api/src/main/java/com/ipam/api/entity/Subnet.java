package com.ipam.api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subnets")
public class Subnet extends NetworkObject {

  private String name;
  private String cidr; // CIDR notation, e.g., "192.168.1.0/24"
  private String mask; // Subnet mask
  private String gateway; // Default gateway for the subnet

  @OneToMany(cascade = CascadeType.ALL)
  private List<IPAddress> ipAddresses = new ArrayList<>();
}
