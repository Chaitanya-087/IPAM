package com.ipam.api.service;

import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IPRangeService {

  @Autowired
  private IPRangeRepository ipRangeRepository;

  @Autowired
  private UserRepository userRepository;

  public IPRangeDTO save(IPRange body) {
    IPRange ipRange = new IPRange();
    ipRange.setStartAddress(body.getStartAddress());
    ipRange.setEndAddress(body.getEndAddress());
    return convertToDTO(ipRangeRepository.save(ipRange));
  }

  public List<IPRangeDTO> findAll() {
    return ipRangeRepository
      .findAll()
      .stream()
      .map(this::convertToDTO)
      .toList();
  }

  public List<IPRangeDTO> findAllAvailable() {
    return ipRangeRepository
      .findByStatus(Status.AVAILABLE)
      .stream()
      .map(this::convertToDTO)
      .toList();
  }

  public List<IPRangeDTO> findByUserId(Long userId) {
    return ipRangeRepository
      .findByUserId(userId)
      .stream()
      .map(this::convertToDTO)
      .toList();
  }

    public String allocate(Long ipAddressId, Long userId) {
    Optional<IPRange> ipAddressOpt = ipRangeRepository.findById(ipAddressId);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (ipAddressOpt.isPresent() && ipAddressOpt.get().getStatus().equals(Status.AVAILABLE)) {
      IPRange ipRange = ipAddressOpt.get();
      ipRange.setStatus(Status.IN_USE);
      ipRange.setExpiration(LocalDateTime.now().plusHours(1));
      ipRange.setUser(userRepository.findById(userId).get());
      ipRangeRepository.save(ipRange);
      return "iprange allocated with expiration date - " + ipRange.getExpiration();
    }
    return "Invalid operation";
  }

  private long calculateIPv4RangeSize(String startAddress, String endAddress) {
    String[] startParts = startAddress.split("\\.");
    String[] endParts = endAddress.split("\\.");

    long start = 0;
    long end = 0;

    for (int i = 0; i < 4; i++) {
      start = start << 8 | Integer.parseInt(startParts[i]);
      end = end << 8 | Integer.parseInt(endParts[i]);
    }

    return (end - start) + 1;
  }

  private IPRangeDTO convertToDTO(IPRange ipRange) {
    IPRangeDTO ipRangeDTO = new IPRangeDTO();
    ipRangeDTO.setId(ipRange.getId());
    ipRangeDTO.setStartAddress(ipRange.getStartAddress());
    ipRangeDTO.setEndAddress(ipRange.getEndAddress());
    ipRangeDTO.setStatus(ipRange.getStatus());
    ipRangeDTO.setSize(
      calculateIPv4RangeSize(ipRange.getStartAddress(), ipRange.getEndAddress())
    );
    ipRangeDTO.setCreatedAt(ipRange.getCreatedAt());
    ipRangeDTO.setUpdatedAt(ipRange.getUpdatedAt());
    ipRangeDTO.setExpirationDate(ipRange.getExpiration());
    ipRangeDTO.setUser(ipRange.getUser());
    return ipRangeDTO;
  }
}
