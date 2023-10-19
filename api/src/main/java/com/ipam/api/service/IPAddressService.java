package com.ipam.api.service;

import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IPAddressService {

  @Autowired
  private IPAddressRepository ipAddressRepository;

  @Autowired
  private UserRepository userRepository;

  public IPAddress save(IPAddress body) {
    IPAddress ipAddress = new IPAddress();
    ipAddress.setAddress(body.getAddress());
    return ipAddressRepository.save(ipAddress);
  }

  public List<IPAddress> findAll() {
    return ipAddressRepository.findAll();
  }

  public List<IPAddress> findByUserId(Long userId) {
    return ipAddressRepository.findByUserId(userId);
  }

  public List<IPAddress> findAllAvailable() {
    return ipAddressRepository.findByStatus(Status.AVAILABLE);
  }

  public String allocate(Long ipAddressId, Long userId) {
    Optional<IPAddress> ipAddressOpt = ipAddressRepository.findById(ipAddressId);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (ipAddressOpt.isPresent() && ipAddressOpt.get().getStatus().equals(Status.AVAILABLE)) {
      IPAddress ipAddress = ipAddressOpt.get();
      ipAddress.setStatus(Status.IN_USE);
      ipAddress.setExpiration(LocalDateTime.now().plusHours(1));
      ipAddress.setUser(userRepository.findById(userId).get());
      ipAddressRepository.save(ipAddress);
      return "ipaddress allocated with expiration date - " + ipAddress.getExpiration();
    }

    return "Invalid operation";
  }
}
