package com.ipam.api.service;

import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.IPRange;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPRangeRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

  @Autowired
  private NetworkUtil networkUtil;

  @Transactional
  public IPRangeDTO save(IPRange body) {
    IPRange ipRange = new IPRange();
    ipRange.setStartAddress(body.getStartAddress());
    ipRange.setEndAddress(body.getEndAddress());
    long start = networkUtil.ipToLong(body.getStartAddress());
    long end = networkUtil.ipToLong(body.getEndAddress());

    for (long current = start; current <= end; current++) {
      String ip = networkUtil.longToIp(current);
      IPAddress ipAddress = new IPAddress();
      ipAddress.setAddress(ip);
      ipRange.getIpAddresses().add(ipAddress);
    }
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

  @Transactional
  public String allocate(Long ipRangeId, Long userId) {
    Optional<IPRange> ipRangeOpt = ipRangeRepository.findById(ipRangeId);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (ipRangeOpt.isPresent()) {
      IPRange ipRange = ipRangeOpt.get();
      for (IPAddress ipAddress : ipRange.getIpAddresses()) {
        if (ipAddress.getStatus().equals(Status.AVAILABLE)) {
          ipAddress.setStatus(Status.IN_USE);
          ipAddress.setUser(userOpt.get());
          ipAddress.setExpiration(LocalDateTime.now().plusDays(1));
        }
      }
      ipRange.setExpiration(LocalDateTime.now().plusDays(1));
      ipRange.setStatus(Status.IN_USE);
      ipRange.setUser(userOpt.get());
      ipRangeRepository.save(ipRange);
      return "Ip range allocated";
    }

    return "Invalid operation";
  }

  public List<IPAddress> findAllIpAddress(Long ipRangeId) {
    Optional<IPRange> ipRangeOpt = ipRangeRepository.findById(ipRangeId);
    if (ipRangeOpt.isPresent()) {
      return ipRangeOpt.get().getIpAddresses();
    }
    return new ArrayList<>();
  }

  public List<IPAddress> findAllAvailableAddressesInRange(Long ipRangeId) {
    return this.findAllIpAddress(ipRangeId)
      .stream()
      .filter(ipAddress -> ipAddress.getStatus().equals(Status.AVAILABLE))
      .toList();
  }

  private IPRangeDTO convertToDTO(IPRange ipRange) {
    IPRangeDTO ipRangeDTO = new IPRangeDTO();
    ipRangeDTO.setId(ipRange.getId());
    ipRangeDTO.setStartAddress(ipRange.getStartAddress());
    ipRangeDTO.setEndAddress(ipRange.getEndAddress());
    ipRangeDTO.setStatus(ipRange.getStatus());
    ipRangeDTO.setSize((long) ipRange.getIpAddresses().size());
    ipRangeDTO.setCreatedAt(ipRange.getCreatedAt());
    ipRangeDTO.setUpdatedAt(ipRange.getUpdatedAt());
    ipRangeDTO.setExpirationDate(ipRange.getExpiration());
    ipRangeDTO.setUser(ipRange.getUser());
    return ipRangeDTO;
  }

  public StatDTO getStats() {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(ipRangeRepository.countByStatus(Status.AVAILABLE));
    stat.setInuseCount(ipRangeRepository.countByStatus(Status.IN_USE));
    stat.setReservedCount(ipRangeRepository.countByStatus(Status.RESERVED));
    return stat;
  }
}
