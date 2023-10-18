package com.ipam.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dnsRecords")
public class DNSRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String hostname;
  private String recordType; // e.g., A, CNAME, PTR

  @ManyToOne
  @JoinColumn(name = "ipAddressId")
  private IPAddress ipAddress;

  @ManyToOne
  @JoinColumn(name = "userId")
  private User user;
}
