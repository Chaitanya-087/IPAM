package com.ipam.api.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "subnets")
public class Subnet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String subnetName; 
  private String subnetCIDR; // CIDR notation, e.g., "192.168.1.0/24"
  private String subnetMask; // Subnet mask
  private String gateway; // Default gateway for the subnet
  private String status;
  
  @OneToMany(cascade = CascadeType.ALL)
  private List<IPAddress> ipAddresses;
}
