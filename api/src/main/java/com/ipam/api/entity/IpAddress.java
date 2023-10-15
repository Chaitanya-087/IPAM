package com.ipam.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Table(name = "ip_addresses")
@Entity
public class IpAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String address;
  private String status; // Available, In Use, Reserved
  private String deviceName;

  @CreationTimestamp
  private Instant createdAt; // Name of the device using the IP

  @UpdateTimestamp
  private Instant updatedAt; // Last time the IP was updated
}
