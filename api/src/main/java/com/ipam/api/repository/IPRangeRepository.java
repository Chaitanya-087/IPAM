package com.ipam.api.repository;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPRangeRepository extends JpaRepository<IPRange, Long> {
  Long countByUser(User user);
  Page<IPRange> findByUserId(Long id, Pageable pageable);
  Page<IPRange> findByStatus(Status status, Pageable pageable);
  long countByStatus(Status status);


  @Query("SELECT ipa FROM IPRange ip JOIN ip.ipAddresses ipa WHERE ip.id = ?1")
  Page<IPAddress> findAllIpAddressesByRangeId(Long id, Pageable pageable);
  boolean existsById(Long id);
}
