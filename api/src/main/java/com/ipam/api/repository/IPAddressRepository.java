package com.ipam.api.repository;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ipam.api.entity.User;


public interface IPAddressRepository extends JpaRepository<IPAddress, Long> {
    Long countByUser(User user);
    List<IPAddress> findByUserId(Long id);
    List<IPAddress> findByStatus(Status status);
}
