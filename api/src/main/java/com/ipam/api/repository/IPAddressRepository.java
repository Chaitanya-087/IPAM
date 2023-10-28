package com.ipam.api.repository;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ipam.api.entity.User;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long> {
    Long countByUser(User user);
    Page<IPAddress> findByUserId(Long userId, Pageable pageable);
    Page<IPAddress> findByStatus(Status status, Pageable pageable);
    long countByStatus(Status status);
    boolean existsByAddress(String address);
}
