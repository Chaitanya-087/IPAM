package com.ipam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipam.api.entity.NetworkObject;
import com.ipam.api.entity.Status;

public interface NetworkObjectRepository extends JpaRepository<NetworkObject, Long> {
    List<NetworkObject> findByStatus(Status status);
}
