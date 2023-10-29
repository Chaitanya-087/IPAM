package com.ipam.api.service;

import com.ipam.api.controller.exception.AlreadyExistException;
import com.ipam.api.controller.exception.InvalidException;
import com.ipam.api.controller.exception.NotFoundException;
import com.ipam.api.dto.IPAddressForm;
import com.ipam.api.dto.PageResponse;
import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;
import com.ipam.api.util.NetworkUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class IPAddressService {

  @Autowired
  private IPAddressRepository ipAddressRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private NetworkUtil networkUtil;

  private Random random = new Random();

  public IPAddress save(IPAddressForm body) {
    IPAddress ipAddress = new IPAddress();
    if (
      body.getAddress() == null ||
      body.getAddress().isEmpty() ||
      !networkUtil.isValidIp(body.getAddress())
    ) {
      throw new InvalidException("Invalid IP Address");
    }
    if (ipAddressRepository.existsByAddress(body.getAddress())) {
      throw new AlreadyExistException("IP Address already exists");
    }
    ipAddress.setAddress(body.getAddress());
    return ipAddressRepository.save(ipAddress);
  }

  public PageResponse<IPAddress> findAll(int pageNumber, int pageSize) {
    return convertToPageResponse(
      ipAddressRepository.findAll(PageRequest.of(pageNumber, pageSize))
    );
  }

  public PageResponse<IPAddress> findByUserId(Long userId, int page, int size) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("user not found");
    }
    Pageable p = PageRequest.of(page, size);
    return convertToPageResponse(ipAddressRepository.findByUserId(userId, p));
  }

  public PageResponse<IPAddress> findAllAvailable(int page, int size) {
    return convertToPageResponse(ipAddressRepository.findByStatus(Status.AVAILABLE, PageRequest.of(page, size)));
  }

  public String allocate(Long ipAddressId, Long userId) {
    Optional<IPAddress> ipAddressOpt = ipAddressRepository.findById(
      ipAddressId
    );
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      return "Invalid user";
    }
    if (
      ipAddressOpt.isPresent() &&
      ipAddressOpt.get().getStatus().equals(Status.AVAILABLE)
    ) {
      IPAddress ipAddress = ipAddressOpt.get();
      ipAddress.setStatus(Status.IN_USE);
      ipAddress.setExpiration(LocalDateTime.now().plusHours(1));
      ipAddress.setUser(userRepository.findById(userId).get());
      ipAddressRepository.save(ipAddress);
      return (
        "ipaddress allocated with expiration date - " +
        ipAddress.getExpiration()
      );
    }

    return "Invalid operation";
  }

  public String assignDomainName(long ipAddresId) throws IOException {
    Optional<IPAddress> ipAddressOpt = ipAddressRepository.findById(ipAddresId);
    if (ipAddressOpt.isPresent() && ipAddressOpt.get().getStatus().equals(Status.IN_USE)) {
      IPAddress ipAddress = ipAddressOpt.get();
      ipAddress.setDns(generateRandomString().concat(".pro"));
      ipAddressRepository.save(ipAddress);
      return "Domain name assigned";
    }
    return "Invalid operation";
  }

  private String generateRandomString() throws IOException {
    String salt = String.valueOf(random.nextInt(10, 100));
    InputStream inputStream = resourceLoader
      .getResource("classpath:words.txt")
      .getInputStream();
    Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
    List<String> lines = scanner.tokens().toList();
    int randomIndex = random.nextInt(lines.size());
    scanner.close();
    return lines.get(randomIndex).concat(salt);
  }

  public StatDTO getStats() {
    StatDTO stat = new StatDTO();
    stat.setAvailableCount(ipAddressRepository.countByStatus(Status.AVAILABLE));
    stat.setInuseCount(ipAddressRepository.countByStatus(Status.IN_USE));
    stat.setReservedCount(ipAddressRepository.countByStatus(Status.RESERVED));
    return stat;
  }

  private PageResponse<IPAddress> convertToPageResponse(Page<IPAddress> page) {
    PageResponse<IPAddress> response = new PageResponse<>();
    response.setTotalPages(page.getTotalPages());
    response.setCurrentPage(page.getNumber());
    response.setHasNext(page.hasNext());
    response.setHasPrevious(page.hasPrevious());
    response.setData(page.getContent());
    response.setTotalElements(page.getTotalElements());
    response.setMaxPageSize(page.getSize());
    response.setCurrentPageSize(page.getNumberOfElements());
    return response;
  }
}
