package com.ipam.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ipam.api.dto.StatDTO;
import com.ipam.api.entity.IPAddress;
import com.ipam.api.entity.Status;
import com.ipam.api.entity.User;
import com.ipam.api.repository.IPAddressRepository;
import com.ipam.api.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class IPAddressServiceUnitTest {

  @InjectMocks
  private IPAddressService ipAddressService;

  @Mock
  private IPAddressRepository ipAddressRepository;

  private IPAddress ipAddress;

  @Mock
  private Random rand;

  @Mock
  private ResourceLoader resourceLoader;

  @Mock
  private UserRepository userRepository;

  @BeforeAll
  public void setUp() {
    ipAddress = new IPAddress();
    ipAddress.setId(1l);
    ipAddress.setUser(null);
    ipAddress.setStatus(Status.AVAILABLE);
    ipAddress.setCreatedAt(LocalDateTime.now());
    ipAddress.setUpdatedAt(LocalDateTime.now());
    ipAddress.setExpiration(null);
    ipAddress.setDns(null);
    ipAddress.setAddress("192.78.9.1");
  }

  @Test
  void givenIpAddress_whenSaved_thenReturnIpAddress() {
    given(ipAddressRepository.save(any(IPAddress.class))).willReturn(ipAddress);

    IPAddress result = ipAddressService.save(ipAddress);

    assertEquals(ipAddress.getAddress(), result.getAddress());
  }

  @Test
  void givenIpAddresses_whenFindAll_thenReturnIpAddresses() {
    given(ipAddressRepository.findAll()).willReturn(List.of(ipAddress));

    List<IPAddress> ipAddresses = ipAddressService.findAll();

    assertEquals(1, ipAddresses.size());
  }

  @Test
  void givenUserId_whenFindByUserId_thenReturnIpAddresses() {
    Long userId = 123L;

    given(ipAddressRepository.findByUserId(userId))
      .willReturn(List.of(ipAddress));

    List<IPAddress> ipAddresses = ipAddressService.findByUserId(userId);

    assertEquals(1, ipAddresses.size());
    assertEquals(ipAddresses.get(0).getAddress(), ipAddress.getAddress());
  }

  @Test
  void testAllocateValidIPAddressAndUser() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    IPAddress ipAddress = new IPAddress();
    ipAddress.setStatus(Status.AVAILABLE);

    User user = new User();

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(ipAddress));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(ipAddressRepository.save(any(IPAddress.class))).willReturn(ipAddress);

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals(
      "ipaddress allocated with expiration date - " + ipAddress.getExpiration(),
      result
    );
    assertEquals(Status.IN_USE, ipAddress.getStatus());
    assertEquals(user, ipAddress.getUser());
  }

  @Test
  void givenAvailableIPAddresses_whenFindAllAvailable_thenReturnAvailableIPAddresses() {
    ipAddress.setStatus(Status.AVAILABLE);
    given(ipAddressRepository.findByStatus(Status.AVAILABLE))
      .willReturn(List.of(ipAddress));

    List<IPAddress> result = ipAddressService.findAllAvailable();

    assertEquals(1, result.size());
    assertEquals(ipAddress.getAddress(), result.get(0).getAddress());
  }

  @Test
  void testAllocateInvalidIPAddress() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    IPAddress ipAddress = new IPAddress();
    ipAddress.setStatus(Status.IN_USE);

    User user = new User();

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(ipAddress));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void testAllocateInvalidUser() {
    Long ipAddressId = 1L;
    Long userId = 2L;

    given(ipAddressRepository.findById(ipAddressId))
      .willReturn(Optional.of(new IPAddress()));
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    String result = ipAddressService.allocate(ipAddressId, userId);

    assertEquals("Invalid user", result);
  }

  @Test
  void givenValidIPAddressId_whenAssignDomainName_thenDomainNameAssigned()
    throws IOException {
    String testWord = "test";
    InputStream inputStream = new ByteArrayInputStream(testWord.getBytes());
    Resource resource = mock(Resource.class);

    when(ipAddressRepository.findById(any(Long.class)))
      .thenReturn(Optional.of(ipAddress));
    when(ipAddressRepository.save(any(IPAddress.class))).thenReturn(ipAddress);
    when(resource.getInputStream()).thenReturn(inputStream);
    when(resourceLoader.getResource(any(String.class))).thenReturn(resource);
    String result = ipAddressService.assignDomainName(1L);

    assertEquals("Domain name assigned", result);
    assertTrue(ipAddress.getDns().contains(".pro"));
  }

  @Test
  void givenInvalidIPAddressId_whenAssignDomainName_thenInvalidOperation()
    throws IOException {
    long ipAddressId = 1L;

    when(ipAddressRepository.findById(anyLong())).thenReturn(Optional.empty());

    String result = ipAddressService.assignDomainName(ipAddressId);

    assertEquals("Invalid operation", result);
  }

  @Test
  void givenStats_whenGetStats_thenReturnStats() {
    given(ipAddressRepository.countByStatus(Status.AVAILABLE)).willReturn(1L);
    given(ipAddressRepository.countByStatus(Status.IN_USE)).willReturn(1L);
    given(ipAddressRepository.countByStatus(Status.RESERVED)).willReturn(1L);

    StatDTO stat = ipAddressService.getStats();

    assertEquals(1L, stat.getAvailableCount());
    assertEquals(1L, stat.getInuseCount());
    assertEquals(1L, stat.getReservedCount());
  }
}
