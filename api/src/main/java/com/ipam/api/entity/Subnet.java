package com.ipam.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "subnets")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Subnet extends NetworkObject {

  private String name;
  private String cidr; // CIDR notation, e.g., "192.168.1.0/24"
  private String mask; // Subnet mask
  private String gateway; // Default gateway for the subnet
}
