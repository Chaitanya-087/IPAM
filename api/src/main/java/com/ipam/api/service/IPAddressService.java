package com.ipam.api.service;

import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;
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
import org.springframework.stereotype.Service;

@Service
public class IPAddressService {

  @Autowired
  private IPAddressRepository ipAddressRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ResourceLoader resourceLoader;
  
  private Random random = new Random();

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
    if (ipAddressOpt.isPresent()) {
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
}
