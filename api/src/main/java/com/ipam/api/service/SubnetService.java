package com.ipam.api.service;

import com.ipam.api.controller.exception.AlreadyExistException;
import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.dto.SubnetDTO;
import com.ipam.api.dto.SubnetForm;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.Subnet;
import com.ipam.api.entity.User;
import com.ipam.api.repository.SubnetRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;

import java.time.LocalDateTime;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SubnetService {

  @Autowired
  private SubnetRepository subnetRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private NetworkUtil networkUtil;

  public SubnetDTO save(SubnetForm body) {
    Subnet subnet = new Subnet();
    subnet.setName(body.getName());
    subnet.setCidr(body.getCidr());
    subnet.setMask(body.getMask());
    subnet.setGateway(body.getGateway());

    if (!networkUtil.isValidCidr(body.getCidr())) {
      throw new InvalidException("Invalid CIDR");
    }

    if (subnetRepository.existsByCidr(body.getCidr())) {
      throw new AlreadyExistException("Subnet already exists");
    }

    String[] parts = body.getCidr().split("/");
    long start = networkUtil.ipToLong(parts[0]);
    int mask = Integer.parseInt(parts[1]);
    long subnetSize = 1L << (32 - mask);
    for (int i = 1; i < subnetSize - 1; i++) {
      String ip = networkUtil.longToIp(start + i);
      IPAddress ipAddress = new IPAddress();
      ipAddress.setAddress(ip);
      subnet.getIpAddresses().add(ipAddress);
    }
    return convertToDto(subnetRepository.save(subnet));
  }

  public PageResponse<Subnet> findAll(int page, int size) {
    return convertToPageResponse(subnetRepository.findAll(PageRequest.of(page,size)));
  }

  public PageResponse<Subnet> findByUserId(Long userId, int page, int size) {
    if (!userRepository.existsById(userId)) {
      throw new InvalidException("user not found");
    }
    return convertToPageResponse(subnetRepository.findByUserId(userId, PageRequest.of(page,size)));
  }

  public  PageResponse<Subnet> findAllAvailable(int page, int size) {
    return convertToPageResponse(subnetRepository.findByStatus(Status.AVAILABLE, PageRequest.of(page, size)));
  }

  public String allocate(Long ipAddressId, Long userId) {
    Optional<Subnet> subnetOpt = subnetRepository.findById(ipAddressId);
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (
      subnetOpt.isPresent() &&
      subnetOpt.get().getStatus().equals(Status.AVAILABLE)
    ) {
      Subnet subnet = subnetOpt.get();
      for (IPAddress ipAddress : subnet.getIpAddresses()) {
        if (ipAddress.getStatus().equals(Status.AVAILABLE)) {
          ipAddress.setStatus(Status.IN_USE);
          ipAddress.setUser(userOpt.get());
          ipAddress.setExpiration(LocalDateTime.now().plusDays(1));
        }
      }
      subnet.setStatus(Status.IN_USE);
      subnet.setExpiration(LocalDateTime.now().plusDays(1));
      subnet.setUser(userRepository.findById(userId).get());
      subnetRepository.save(subnet);
      return "Subnet is allocated";
    }
    return "Invalid operation";
  }

  public StatDTO getStats() {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(subnetRepository.countByStatus(Status.AVAILABLE));
    stat.setInuseCount(subnetRepository.countByStatus(Status.IN_USE));
    stat.setReservedCount(subnetRepository.countByStatus(Status.RESERVED));
    return stat;
  }

  private SubnetDTO convertToDto(Subnet subnet) {
    SubnetDTO subnetDTO = new SubnetDTO();
    subnetDTO.setId(subnet.getId());
    subnetDTO.setUser(subnet.getUser());
    subnetDTO.setStatus(subnet.getStatus());
    subnetDTO.setCreatedAt(subnet.getCreatedAt());
    subnetDTO.setUpdatedAt(subnet.getUpdatedAt());
    subnetDTO.setExpiration(subnet.getExpiration());
    subnetDTO.setName(subnet.getName());
    subnetDTO.setCidr(subnet.getCidr());
    subnetDTO.setMask(subnet.getMask());
    subnetDTO.setGateway(subnet.getGateway());
    subnetDTO.setSize((long) subnet.getIpAddresses().size());
    return subnetDTO;
  }


    private PageResponse<Subnet> convertToPageResponse(Page<Subnet> page) {
    PageResponse<Subnet> response = new PageResponse<>();
    response.setTotalPages(page.getTotalPages());
    response.setCurrentPage(page.getNumber());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());
    response.setData(page.getContent().stream().map(this::convertToDto).toList());
    response.setTotalElements(page.getTotalElements());
    response.setMaxPageSize(page.getSize());
    response.setCurrentPageSize(page.getNumberOfElements());
    return response;
  }
}
