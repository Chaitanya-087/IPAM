package com.ipam.api.repository;

import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPRangeRepository extends JpaRepository<IPRange, Long> {
  Long countByUser(User user);
  List<IPRange> findByUserId(Long id);
  List<IPRange> findByStatus(Status status);
  long countByStatus(Status status);
}
