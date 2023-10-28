package com.ipam.api.service;

import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.controller.exception.NotFoundException;
import com.ipam.api.dto.IPRangeDTO;
import com.ipam.api.dto.IPRangeForm;
import com.ipam.api.dto.PageResponse;
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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
  public IPRangeDTO save(IPRangeForm body) {
    if (!networkUtil.isValidIp(body.getStartAddress()) || !networkUtil.isValidIp(body.getEndAddress())) {
      throw new InvalidException("Invalid IP Range");
    }
    IPRange ipRange = new IPRange();
    ipRange.setStartAddress(body.getStartAddress());
    ipRange.setEndAddress(body.getEndAddress());

    long start = networkUtil.ipToLong(body.getStartAddress());
    long end = networkUtil.ipToLong(body.getEndAddress());

    if (start > end) {
      throw new InvalidException("Start address must be less than end address");
    }

    for (long current = start; current <= end; current++) {
      String ip = networkUtil.longToIp(current);
      IPAddress ipAddress = new IPAddress();
      ipAddress.setAddress(ip);
      ipRange.getIpAddresses().add(ipAddress);
    }
    return convertToDTO(ipRangeRepository.save(ipRange));
  }

  public PageResponse<IPRange> findAll(int page, int size) {
    return convertToPageResponseIpRange(
      ipRangeRepository.findAll(PageRequest.of(page, size))
    );
  }

  public PageResponse<IPRange> findAllAvailable(int page, int size) {
    return convertToPageResponseIpRange(
      ipRangeRepository.findByStatus(
        Status.AVAILABLE,
        PageRequest.of(page, size)
      )
    );
  }

  public PageResponse<IPRange> findByUserId(Long userId, int page, int size) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("User not found");
    }
    return convertToPageResponseIpRange(
      ipRangeRepository.findByUserId(userId, PageRequest.of(page, size))
    );
  }

  @Transactional
  public String allocate(Long ipRangeId, Long userId) {
    Optional<IPRange> ipRangeOpt = ipRangeRepository.findById(ipRangeId);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (
      ipRangeOpt.isPresent() &&
      ipRangeOpt.get().getStatus().equals(Status.AVAILABLE)
    ) {
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

  public PageResponse<IPAddress> findAllIpAddress(
    Long ipRangeId,
    int page,
    int size
  ) {
    if (!ipRangeRepository.existsById(ipRangeId)) {
      throw new NotFoundException("Ip range not found");
    }
    return convertToPageResponseIpAddress(
      ipRangeRepository.findAllIpAddressesByRangeId(
        ipRangeId,
        PageRequest.of(page, size)
      ),"all"
    );
  }

  public PageResponse<IPAddress> findAllAvailableAddressesInRange(
    Long ipRangeId,
    int page,
    int size
  ) {
    if (!ipRangeRepository.existsById(ipRangeId)) {
      throw new NotFoundException("Ip range not found");
    }
    return convertToPageResponseIpAddress(
      ipRangeRepository.findAllIpAddressesByRangeId(
        ipRangeId,
        PageRequest.of(page, size)
      ),"available"
    );
  }

  public StatDTO getStats() {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(ipRangeRepository.countByStatus(Status.AVAILABLE));
    stat.setInuseCount(ipRangeRepository.countByStatus(Status.IN_USE));
    stat.setReservedCount(ipRangeRepository.countByStatus(Status.RESERVED));
    return stat;
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

  private PageResponse<IPRange> convertToPageResponseIpRange(
    Page<IPRange> page
  ) {
    PageResponse<IPRange> response = new PageResponse<>();
    response.setTotalPages(page.getTotalPages());
    response.setCurrentPage(page.getNumber());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());
    response.setData(
      page.getContent().stream().map(this::convertToDTO).toList()
    );
    response.setTotalElements(page.getTotalElements());
    response.setMaxPageSize(page.getSize());
    response.setCurrentPageSize(page.getNumberOfElements());
    return response;
  }

  private PageResponse<IPAddress> convertToPageResponseIpAddress(Page<IPAddress> page, String type) {
    PageResponse<IPAddress> response = new PageResponse<>();
    response.setTotalPages(page.getTotalPages());
    response.setCurrentPage(page.getNumber());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());
    if (type.equals("available")) {
      response.setData(page.getContent().stream().filter(ip -> ip.getStatus().equals(Status.AVAILABLE)).toList());
    } else {
      response.setData(page.getContent());
    }
    response.setTotalElements(page.getTotalElements());
    response.setMaxPageSize(page.getSize());
    response.setCurrentPageSize(page.getNumberOfElements());
    return response;
  }

}
