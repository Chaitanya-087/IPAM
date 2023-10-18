package com.ipam.api.repository;

import com.ipam.api.entity.DNSRecord;
import com.ipam.api.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DNSRecordRepository extends JpaRepository<DNSRecord, Long> {
  Long countByUser(User user);
}
