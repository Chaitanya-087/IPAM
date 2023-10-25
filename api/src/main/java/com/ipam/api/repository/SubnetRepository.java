package com.ipam.api.repository;

import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubnetRepository extends JpaRepository<Subnet, Long> {
  Long countByUser(User user);
  List<Subnet> findByUserId(Long id);
  List<Subnet> findByStatus(Status status);
  long countByStatus(Status status);
}
