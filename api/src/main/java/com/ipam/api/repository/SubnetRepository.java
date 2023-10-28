package com.ipam.api.repository;

import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubnetRepository extends JpaRepository<Subnet, Long> {
  Long countByUser(User user);
  Page<Subnet> findByUserId(Long id, Pageable pageable);
  Page<Subnet> findByStatus(Status status, Pageable pageable);
  long countByStatus(Status status);
  boolean existsByCidr(String cidr);
}
