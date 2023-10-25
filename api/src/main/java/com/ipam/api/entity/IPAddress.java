package com.ipam.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "ipAddresses")
@Entity
public class IPAddress extends NetworkObject {
  private String dns;
  private String address;
}
